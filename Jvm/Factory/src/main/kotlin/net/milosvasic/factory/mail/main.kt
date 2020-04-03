@file:JvmName("Launcher")

package net.milosvasic.factory.mail

import com.google.gson.Gson
import com.google.gson.JsonParseException
import net.milosvasic.factory.mail.component.installer.Installer
import net.milosvasic.factory.mail.component.installer.InstallerInitializationOperation
import net.milosvasic.factory.mail.component.installer.InstallerOperation
import net.milosvasic.factory.mail.configuration.Configuration
import net.milosvasic.factory.mail.configuration.SoftwareConfiguration
import net.milosvasic.factory.mail.error.ERROR
import net.milosvasic.factory.mail.operation.Command
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.operation.OperationResultListener
import net.milosvasic.factory.mail.os.OSType
import net.milosvasic.factory.mail.processor.ServiceProcessor
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
            log.v("Configuration file: ${configurationFile.absolutePath}")
            val configurationJson = configurationFile.readText()
            val gson = Gson()
            try {
                val configuration = gson.fromJson(configurationJson, Configuration::class.java)
                val softwareConfiguration = SoftwareConfiguration.obtain(configuration.softwareConfiguration)
                log.v(configuration.name)

                val host = configuration.remote.host
                val ssh = SSH(configuration.remote)
                val terminal = ssh.terminal
                val installer = Installer(softwareConfiguration, ssh)
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
                                            val os = ssh.getRemoteOS()
                                            os.parseAndSetSystemInfo(result.data)
                                            if (os.getType() == OSType.UNKNOWN) {
                                                log.w("Host operating system is unknown")
                                            } else {
                                                log.i("Host operating system: ${ssh.getRemoteOS().getName()}")
                                            }

                                            installer.subscribe(this)
                                            installer.initialize()
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
                            is InstallerInitializationOperation -> {

                                if (result.success) {

                                    log.i("Installer is ready")
                                    installer.install()
                                } else {

                                    log.e("Could not initialize installer")
                                    fail(ERROR.INITIALIZATION_FAILURE)
                                }
                            }
                            is InstallerOperation -> {

                                installer.terminate()
                                if (result.success) {

                                    // TODO: Handle services through the flow.
                                    configuration.services.forEach {
                                        processor.process(it)
                                    }
                                    finish()
                                } else {

                                    log.e("Could not perform installation")
                                    fail(ERROR.INSTALLATION_FAILURE)
                                }
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
            } catch (e: JsonParseException) {
                fail(e)
            } catch (e: IllegalArgumentException) {
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