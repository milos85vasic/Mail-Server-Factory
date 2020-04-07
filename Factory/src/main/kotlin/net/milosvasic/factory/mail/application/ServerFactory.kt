package net.milosvasic.factory.mail.application

import com.google.gson.Gson
import com.google.gson.JsonParseException
import net.milosvasic.factory.mail.common.Application
import net.milosvasic.factory.mail.common.busy.BusyException
import net.milosvasic.factory.mail.component.installer.Installer
import net.milosvasic.factory.mail.component.installer.InstallerInitializationOperation
import net.milosvasic.factory.mail.component.installer.InstallerOperation
import net.milosvasic.factory.mail.configuration.Configuration
import net.milosvasic.factory.mail.configuration.SoftwareConfiguration
import net.milosvasic.factory.mail.error.ERROR
import net.milosvasic.factory.mail.fail
import net.milosvasic.factory.mail.log
import net.milosvasic.factory.mail.operation.Command
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.operation.OperationResultListener
import net.milosvasic.factory.mail.os.Architecture
import net.milosvasic.factory.mail.os.OSType
import net.milosvasic.factory.mail.processor.ServiceProcessor
import net.milosvasic.factory.mail.remote.ssh.SSH
import net.milosvasic.factory.mail.remote.ssh.SSHCommand
import net.milosvasic.factory.mail.terminal.Commands
import java.io.File
import kotlin.system.exitProcess

class ServerFactory : Application {

    override fun run(args: Array<String>) {

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
                    val softwareConfigurations = mutableListOf<SoftwareConfiguration>()
                    configuration.software.forEach {
                        val softwareConfiguration = SoftwareConfiguration.obtain(it)
                        softwareConfigurations.add(softwareConfiguration)
                    }
                    log.v(configuration.name)

                    val host = configuration.remote.host
                    val ssh = SSH(configuration.remote)
                    val terminal = ssh.terminal
                    val installer = Installer(ssh)
                    val processor = ServiceProcessor(ssh)
                    val pingCommand = Command(Commands.ping(host))
                    val testCommand = Commands.echo("Hello")
                    val hostInfoCommand = Commands.getHostInfo()
                    var softwareConfigurationsIterator: Iterator<SoftwareConfiguration>? = null

                    fun tryNext() {
                        softwareConfigurationsIterator?.let {

                            if (it.hasNext()) {
                                val softwareConfiguration = it.next()
                                try {
                                    installer.setConfiguration(softwareConfiguration)
                                    installer.install()
                                } catch (e: BusyException) {

                                    fail(e)
                                }
                            } else {

                                installer.terminate()

                                // TODO: Continue the flow.
                                configuration.services.forEach { service ->
                                    processor.process(service)
                                }
                                finish()
                            }
                        }
                    }

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
                                                if (os.getArchitecture() == Architecture.UNKNOWN) {
                                                    log.w("Host system architecture is unknown")
                                                } else {
                                                    val arch = ssh.getRemoteOS().getArchitecture().arch.toUpperCase()
                                                    log.i("Host system architecture: $arch")
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
                                        softwareConfigurationsIterator = softwareConfigurations.iterator()
                                        tryNext()
                                    } else {

                                        log.e("Could not initialize installer")
                                        fail(ERROR.INITIALIZATION_FAILURE)
                                    }
                                }
                                is InstallerOperation -> {

                                    if (result.success) {
                                        tryNext()
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

    override fun onStop() {
        log.i("FINISHED")
        exitProcess(0)
    }

    private fun finish() {
        onStop()
    }
}