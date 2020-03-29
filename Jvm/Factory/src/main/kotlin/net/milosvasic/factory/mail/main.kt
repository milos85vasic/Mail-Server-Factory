@file:JvmName("Launcher")

package net.milosvasic.factory.mail

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import net.milosvasic.factory.mail.component.packaging.Dnf
import net.milosvasic.factory.mail.component.packaging.PackageManagerOperation
import net.milosvasic.factory.mail.component.packaging.item.Packages
import net.milosvasic.factory.mail.component.packaging.item.Package
import net.milosvasic.factory.mail.component.packaging.item.Envelope
import net.milosvasic.factory.mail.configuration.Configuration
import net.milosvasic.factory.mail.error.ERROR
import net.milosvasic.factory.mail.operation.Command
import net.milosvasic.factory.mail.processor.ServiceProcessor
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.operation.OperationResultListener
import net.milosvasic.factory.mail.os.OSType
import net.milosvasic.factory.mail.remote.ssh.SSH
import net.milosvasic.factory.mail.remote.ssh.SSHCommand
import net.milosvasic.factory.mail.terminal.Commands
import net.milosvasic.logger.ConsoleLogger
import net.milosvasic.logger.FilesystemLogger
import java.io.File
import kotlin.system.exitProcess

fun main(args: Array<String>) {

    initLogging()
    log.i("STARTED")
    if (args.isEmpty()) {

        fail(ERROR.EMPTY_DATA)
    } else {

        val configurationFileName = args[0]
        val configurationFile = File(configurationFileName)
        if (configurationFile.exists()) {
            log.v("Configuration file: $configurationFileName")
            val configurationJson = configurationFile.readText()
            val gson = Gson()
            try {
                val configuration = gson.fromJson(configurationJson, Configuration::class.java)
                log.v(configuration.name)

                val host = configuration.remote.host
                val ssh = SSH(configuration.remote)
                val terminal = ssh.terminal
                val dnf = Dnf(ssh) // TODO: Remove when not needed anymore - after tryout.
                val processor = ServiceProcessor(ssh)
                val pingCommand = Command(Commands.ping(host))
                val testCommand = Commands.echo("Hello")
                val hostInfoCommand = Commands.getHostInfo()

                val listener = object : OperationResultListener {
                    override fun onOperationPerformed(result: OperationResult) {
                        when (result.operation) {
                            is SSHCommand -> {
                                when (result.operation.command) {
                                    hostInfoCommand -> {
                                        if (result.success) {
                                            val os = ssh.operatingSystem
                                            os.parseAndSetSystemInfo(result.data)
                                            if (os.getType() == OSType.UNKNOWN) {
                                                log.w("Host operating system is unknown")
                                            } else {
                                                log.i("Host operating system: ${ssh.operatingSystem.getName()}")
                                            }


                                            // ============== Dnf tryout

                                            dnf.subscribe(this)
                                            val packed = Envelope("git", "cmake")
                                            dnf.install(
                                                listOf(
                                                    Packages(packed),
                                                    Package("sqlite")
                                                )
                                            )

                                            // ============== Dnf tryout === E N D

                                        } else {

                                            log.e("Could not connect to: ${configuration.remote}")
                                            fail(ERROR.INITIALIZATION_FAILURE)
                                        }
                                    }
                                    testCommand -> {
                                        if (result.success) {
                                            log.v("Connected to: ${configuration.remote}")
                                            ssh.execute(hostInfoCommand, true)
                                        } else {

                                            log.e("Could not connect to: ${configuration.remote}")
                                            fail(ERROR.INITIALIZATION_FAILURE)
                                        }
                                    }
                                    else -> {

                                    }
                                }
                            }
                            is Command -> {
                                when (result.operation.toExecute) {
                                    pingCommand.toExecute -> {
                                        if (result.success) {
                                            ssh.execute(testCommand)
                                        } else {

                                            log.e("Host is unreachable: $host")
                                            fail(ERROR.INITIALIZATION_FAILURE)
                                        }
                                    }
                                }
                            }
                            is PackageManagerOperation -> {

                                dnf.shutdown()
                                configuration.services.forEach {
                                    processor.process(it)
                                }
                                finish()
                            }
                            else -> {

                                log.e("Unexpected operation has been performed: ${result.operation}")
                                fail(ERROR.INITIALIZATION_FAILURE)
                            }
                        }
                    }
                }

                ssh.subscribe(listener)
                terminal.execute(pingCommand)
            } catch (e: JsonSyntaxException) {
                fail(e)
            }
        } else {

            fail(ERROR.FILE_DOES_NOT_EXIST, configurationFile.absolutePath)
        }
    }
}

private fun finish() {
    log.i("FINISHED")
    exitProcess(0)
}

private fun initLogging() {
    val console = ConsoleLogger()
    val filesystem = FilesystemLogger(File("."))
    compositeLogger.addLoggers(console, filesystem)
}