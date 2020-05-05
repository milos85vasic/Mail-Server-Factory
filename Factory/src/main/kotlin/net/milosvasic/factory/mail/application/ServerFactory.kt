package net.milosvasic.factory.mail.application

import net.milosvasic.factory.mail.EMPTY
import net.milosvasic.factory.mail.common.Application
import net.milosvasic.factory.mail.common.busy.BusyException
import net.milosvasic.factory.mail.common.exception.EmptyDataException
import net.milosvasic.factory.mail.component.docker.Docker
import net.milosvasic.factory.mail.component.docker.DockerInitializationOperation
import net.milosvasic.factory.mail.component.docker.DockerOperation
import net.milosvasic.factory.mail.component.installer.Installer
import net.milosvasic.factory.mail.component.installer.InstallerAbstract
import net.milosvasic.factory.mail.component.installer.InstallerInitializationOperation
import net.milosvasic.factory.mail.component.installer.InstallerOperation
import net.milosvasic.factory.mail.configuration.ConfigurationManager
import net.milosvasic.factory.mail.configuration.SoftwareConfiguration
import net.milosvasic.factory.mail.configuration.Variable
import net.milosvasic.factory.mail.configuration.VariableNode
import net.milosvasic.factory.mail.error.ERROR
import net.milosvasic.factory.mail.fail
import net.milosvasic.factory.mail.log
import net.milosvasic.factory.mail.operation.Command
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.operation.OperationResultListener
import net.milosvasic.factory.mail.os.Architecture
import net.milosvasic.factory.mail.os.OSType
import net.milosvasic.factory.mail.remote.ssh.SSH
import net.milosvasic.factory.mail.terminal.Commands
import net.milosvasic.factory.mail.terminal.TerminalCommand
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

                    fun printVariableNode(variableNode: VariableNode?, prefix: String = String.EMPTY) {
                        val prefixEnd = "-> "
                        variableNode?.let { node ->
                            if (node.value != String.EMPTY) {
                                val printablePrefix = if (prefix != String.EMPTY) {
                                    " $prefix $prefixEnd"
                                } else {
                                    " "
                                }
                                val nodeValue = Variable.parse(node.value.toString())
                                log.v("Configuration variable:$printablePrefix${node.name} -> $nodeValue")
                            }
                            node.children.forEach { child ->
                                var nextPrefix = prefix
                                if (nextPrefix != String.EMPTY && !nextPrefix.endsWith(prefixEnd)) {
                                    nextPrefix += " $prefixEnd"
                                }
                                nextPrefix += node.name
                                printVariableNode(child, nextPrefix)
                            }
                        }
                    }
                    printVariableNode(configuration.variables)

                    val softwareConfigurations = mutableListOf<SoftwareConfiguration>()
                    val containersConfigurations = mutableListOf<SoftwareConfiguration>()

                    configuration.software?.forEach {
                        val softwareConfiguration = SoftwareConfiguration.obtain(it)
                        softwareConfigurations.add(softwareConfiguration)
                    }
                    configuration.containers?.forEach {
                        val containerConfiguration = SoftwareConfiguration.obtain(it)
                        containersConfigurations.add(containerConfiguration)
                    }

                    log.v(configuration.name)

                    val host = configuration.remote.host
                    val ssh = SSH(configuration.remote)
                    val docker = Docker(ssh)
                    val terminal = ssh.terminal
                    val installer = Installer(ssh)
                    val pingCommand = TerminalCommand(Commands.ping(host))
                    val hostInfoCommand = TerminalCommand(Commands.getHostInfo())
                    val testCommand = TerminalCommand(Commands.echo("Hello"))
                    var softwareConfigurationsIterator: Iterator<SoftwareConfiguration>? = null
                    var containerConfigurationsIterator: Iterator<SoftwareConfiguration>? = null

                    fun tryNext(iterator: Iterator<SoftwareConfiguration>, installer: InstallerAbstract): Boolean {
                        if (iterator.hasNext()) {
                            val softwareConfiguration = iterator.next()
                            try {
                                installer.setConfiguration(softwareConfiguration)
                                installer.install()
                            } catch (e: BusyException) {

                                fail(e)
                            }
                        } else {
                            return false
                        }
                        return true
                    }

                    fun tryNextContainerConfiguration(): Boolean {
                        var result = false
                        containerConfigurationsIterator?.let {
                            result = tryNext(it, docker)
                            if (!result) {
                                finish()
                            }
                        }
                        return result
                    }

                    fun tryNextSoftwareConfiguration(): Boolean {
                        var result = false
                        softwareConfigurationsIterator?.let {
                            result = tryNext(it, installer)
                            if (!result) {
                                installer.terminate()
                            }
                        }
                        return result
                    }

                    val listener = object : OperationResultListener {
                        override fun onOperationPerformed(result: OperationResult) {
                            when (result.operation) {
                                is TerminalCommand -> {
                                    when (result.operation) {
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
                                        pingCommand -> {
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
                                        nextSoftwareConfiguration()
                                    } else {

                                        log.e("Could not initialize installer")
                                        fail(ERROR.INITIALIZATION_FAILURE)
                                    }
                                }
                                is DockerInitializationOperation -> {

                                    if (result.success) {

                                        log.i("Docker is ready")
                                        containerConfigurationsIterator = containersConfigurations.iterator()
                                        nextContainerConfiguration()
                                    } else {

                                        log.e("Could not initialize Docker")
                                        fail(ERROR.INITIALIZATION_FAILURE)
                                    }
                                }
                                is InstallerOperation -> {

                                    if (result.success) {
                                        nextSoftwareConfiguration()
                                    } else {

                                        log.e("Could not perform installation")
                                        fail(ERROR.INSTALLATION_FAILURE)
                                    }
                                }
                                is DockerOperation -> {

                                    if (result.success) {
                                        nextContainerConfiguration()
                                    } else {

                                        log.e("Could not perform docker operation")
                                        fail(ERROR.INSTALLATION_FAILURE)
                                    }
                                }
                                else -> {

                                    log.e("Unexpected operation has been performed: ${result.operation}")
                                    fail(ERROR.INITIALIZATION_FAILURE)
                                }
                            }
                        }

                        private fun nextSoftwareConfiguration() {
                            if (!tryNextSoftwareConfiguration()) {
                                docker.subscribe(this)
                                docker.initialize()
                            }
                        }

                        private fun nextContainerConfiguration() {
                            if (!tryNextContainerConfiguration()) {
                                docker.unsubscribe(this)
                                docker.terminate()
                            }
                        }
                    }

                    ssh.subscribe(listener)
                    terminal.execute(pingCommand)
                } catch (e: IllegalArgumentException) {

                    fail(e)
                } catch (e: IllegalStateException) {

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