package net.milosvasic.factory.mail.application

import net.milosvasic.factory.mail.common.Application
import net.milosvasic.factory.mail.common.busy.BusyException
import net.milosvasic.factory.mail.common.exception.EmptyDataException
import net.milosvasic.factory.mail.component.docker.Docker
import net.milosvasic.factory.mail.component.installer.*
import net.milosvasic.factory.mail.configuration.ConfigurationManager
import net.milosvasic.factory.mail.configuration.SoftwareConfiguration
import net.milosvasic.factory.mail.error.ERROR
import net.milosvasic.factory.mail.fail
import net.milosvasic.factory.mail.log
import net.milosvasic.factory.mail.operation.Command
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.operation.OperationResultListener
import net.milosvasic.factory.mail.os.Architecture
import net.milosvasic.factory.mail.os.OSType
import net.milosvasic.factory.mail.remote.ssh.SSH
import net.milosvasic.factory.mail.remote.ssh.SSHCommand
import net.milosvasic.factory.mail.terminal.Commands
import kotlin.system.exitProcess

class ServerFactory : Application {

    override fun run(args: Array<String>) {
        log.i("STARTED")
        val argumentsValidator = ArgumentsValidator()
        try {
            if (argumentsValidator.validate(args)) {
                val configurationFile = args[0]
                try {

                    ConfigurationManager.setConfigurationPath(configurationFile)
                    ConfigurationManager.initialize()

                    val configuration = ConfigurationManager.getConfiguration()
                    val softwareConfigurations = mutableListOf<SoftwareConfiguration>()
                    val containersConfiguration = mutableListOf<SoftwareConfiguration>()

                    configuration.software.forEach {
                        val softwareConfiguration = SoftwareConfiguration.obtain(it)
                        softwareConfigurations.add(softwareConfiguration)
                    }
                    configuration.containers.forEach {
                        val containerConfiguration = SoftwareConfiguration.obtain(it)
                        containersConfiguration.add(containerConfiguration)
                    }

                    log.v(configuration.name)

                    val host = configuration.remote.host
                    val ssh = SSH(configuration.remote)
                    val docker = Docker(ssh)
                    val terminal = ssh.terminal
                    val installer = Installer(ssh)
                    val pingCommand = Command(Commands.ping(host))
                    val testCommand = Commands.echo("Hello")
                    val hostInfoCommand = Commands.getHostInfo()
                    var softwareConfigurationsIterator: Iterator<SoftwareConfiguration>? = null
                    var containerConfigurationsIterator: Iterator<SoftwareConfiguration>? = null

                    fun tryNext(iterator: Iterator<SoftwareConfiguration>, installer: InstallerAbstract) {
                        if (iterator.hasNext()) {
                            val softwareConfiguration = iterator.next()
                            try {
                                installer.setConfiguration(softwareConfiguration)
                                installer.install()
                            } catch (e: BusyException) {

                                fail(e)
                            }
                        } else {

                            installer.terminate()
                            finish()
                        }
                    }

                    fun tryNextSoftwareConfiguration() {
                        softwareConfigurationsIterator?.let {
                            tryNext(it, installer)
                        }
                    }

                    fun tryNextContainerConfiguration() {
                        containerConfigurationsIterator?.let {
                            tryNext(it, docker)
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
                                        tryNextSoftwareConfiguration()
                                    } else {

                                        log.e("Could not initialize installer")
                                        fail(ERROR.INITIALIZATION_FAILURE)
                                    }
                                }
                                is InstallerOperation -> {

                                    if (result.success) {
                                        tryNextSoftwareConfiguration()
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
                } catch (e: IllegalArgumentException) {

                    fail(e)
                } catch (e: IllegalArgumentException) {

                    fail(e)
                } catch (e: BusyException) {

                    fail(e)
                }
            } else {

                fail(ERROR.INVALID_DATA)
            }
        } catch (e: EmptyDataException) {

            fail(ERROR.EMPTY_DATA)
        } catch (e: IllegalArgumentException) {

            fail(e)
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