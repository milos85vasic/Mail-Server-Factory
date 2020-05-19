package net.milosvasic.factory.mail.application.server_factory

import net.milosvasic.factory.mail.EMPTY
import net.milosvasic.factory.mail.application.ArgumentsValidator
import net.milosvasic.factory.mail.common.Application
import net.milosvasic.factory.mail.common.busy.Busy
import net.milosvasic.factory.mail.common.busy.BusyDelegation
import net.milosvasic.factory.mail.common.busy.BusyException
import net.milosvasic.factory.mail.common.busy.LegacyBusyWorker
import net.milosvasic.factory.mail.common.exception.EmptyDataException
import net.milosvasic.factory.mail.common.initialization.Termination
import net.milosvasic.factory.mail.component.docker.Docker
import net.milosvasic.factory.mail.component.docker.DockerInitializationFlowCallback
import net.milosvasic.factory.mail.component.installer.Installer
import net.milosvasic.factory.mail.component.installer.InstallerInitializationFlowCallback
import net.milosvasic.factory.mail.configuration.*
import net.milosvasic.factory.mail.execution.flow.callback.DieOnFailureCallback
import net.milosvasic.factory.mail.execution.flow.callback.TerminationCallback
import net.milosvasic.factory.mail.execution.flow.implementation.CommandFlow
import net.milosvasic.factory.mail.execution.flow.implementation.InitializationFlow
import net.milosvasic.factory.mail.execution.flow.implementation.InstallationFlow
import net.milosvasic.factory.mail.fail
import net.milosvasic.factory.mail.log
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.operation.OperationResultListener
import net.milosvasic.factory.mail.os.HostInfoDataHandler
import net.milosvasic.factory.mail.remote.ssh.SSH
import net.milosvasic.factory.mail.terminal.Commands
import net.milosvasic.factory.mail.terminal.TerminalCommand
import java.util.concurrent.ConcurrentLinkedQueue

class ServerFactory(val arguments: List<String> = listOf()) : Application, BusyDelegation {

    private val busy = Busy()
    private var configuration: Configuration? = null
    private val terminators = mutableListOf<Termination>()
    private val terminationOperation = ServerFactoryTerminationOperation()
    private val subscribers = ConcurrentLinkedQueue<OperationResultListener>()
    private val initializationOperation = ServerFactoryInitializationOperation()
    private val softwareConfigurations = mutableListOf<SoftwareConfiguration>()
    private val containersConfigurations = mutableListOf<SoftwareConfiguration>()

    @Throws(IllegalStateException::class)
    override fun initialize() {
        checkInitialized()
        busy()
        val argumentsValidator = ArgumentsValidator()
        try {
            if (argumentsValidator.validate(arguments.toTypedArray())) {
                val configurationFile = arguments[0]
                try {
                    ConfigurationManager.setConfigurationPath(configurationFile)
                    ConfigurationManager.initialize()

                    configuration = ConfigurationManager.getConfiguration()

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
                    if (configuration == null) {
                        throw IllegalStateException("Configuration is null")
                    }
                    configuration?.let { config ->
                        printVariableNode(config.variables)

                        config.software?.forEach {
                            val softwareConfiguration = SoftwareConfiguration.obtain(it)
                            softwareConfigurations.add(softwareConfiguration)
                        }
                        config.containers?.forEach {
                            val containerConfiguration = SoftwareConfiguration.obtain(it)
                            containersConfigurations.add(containerConfiguration)
                        }

                        log.v(config.name)
                        notifyInit(true)
                    }
                } catch (e: IllegalArgumentException) {

                    notifyInit(e)
                } catch (e: IllegalStateException) {

                    notifyInit(e)
                }
            } else {

                log.e("Invalid configuration")
                notifyInit(false)
            }
        } catch (e: EmptyDataException) {

            notifyInit(e)
        } catch (e: IllegalArgumentException) {

            notifyInit(e)
        }
    }

    @Throws(IllegalStateException::class)
    override fun terminate() {
        checkNotInitialized()
        if (!busy.isBusy()) {
            throw IllegalStateException("Server factory is not running")
        }
        try {
            terminators.forEach {
                it.terminate()
            }
            configuration = null
            softwareConfigurations.clear()
            containersConfigurations.clear()
            notifyTerm()
        } catch (e: IllegalStateException) {
            notifyTerm(e)
        }
    }

    @Synchronized
    override fun isInitialized(): Boolean {
        try {
            ConfigurationManager.getConfiguration()
            return true
        } catch (e: IllegalStateException) {
            log.w(e)
        }
        return false
    }

    @Throws(IllegalStateException::class)
    override fun run() {
        checkNotInitialized()
        busy()
        if (configuration == null) {
            throw IllegalStateException("Configuration is null")
        }
        log.i("Server factory started")
        try {
            configuration?.let { config ->

                val ssh = SSH(config.remote)
                val docker = Docker(ssh)
                val installer = Installer(ssh)

                terminators.add(docker)
                terminators.add(installer)

                val dockerFlow = getDockerFlow(docker)
                val dockerInitFlow = getDockerInitFlow(docker, dockerFlow)
                val installFlow = getInstallationFlow(installer, dockerInitFlow)
                val initFlow = getInitializationFlow(installer, installFlow)
                val commandFlow = getCommandFlow(ssh, initFlow)

                commandFlow.run()
            }
        } catch (e: IllegalArgumentException) {

            fail(e)
        } catch (e: IllegalStateException) {

            fail(e)
        }
    }

    override fun onStop() {
        log.i("Server factory finished")
        try {
            terminate()
        } catch (e: IllegalStateException) {
            log.e(e)
        }
    }

    @Synchronized
    @Throws(BusyException::class)
    override fun busy() {
        LegacyBusyWorker.busy(busy)
    }

    @Synchronized
    override fun free() {
        LegacyBusyWorker.free(busy)
    }

    fun isBusy() = busy.isBusy()

    @Synchronized
    @Throws(IllegalStateException::class)
    override fun checkInitialized() {
        if (ConfigurationManager.isInitialized()) {
            throw IllegalStateException("Server factory has been already initialized")
        }
    }

    @Synchronized
    @Throws(IllegalStateException::class)
    override fun checkNotInitialized() {
        if (!ConfigurationManager.isInitialized()) {
            throw IllegalStateException("Server factory has not been initialized")
        }
    }

    override fun subscribe(what: OperationResultListener) {
        subscribers.add(what)
    }

    override fun unsubscribe(what: OperationResultListener) {
        subscribers.remove(what)
    }

    @Synchronized
    override fun notify(data: OperationResult) {
        val iterator = subscribers.iterator()
        while (iterator.hasNext()) {
            val listener = iterator.next()
            listener.onOperationPerformed(data)
        }
    }


    private fun notifyInit(success: Boolean) {
        free()
        val result = OperationResult(initializationOperation, success)
        notify(result)
    }

    private fun notifyTerm() {
        free()
        val result = OperationResult(terminationOperation, true)
        notify(result)
    }

    @Synchronized
    private fun notifyInit(e: Exception) {
        free()
        log.e(e)
        val result = OperationResult(initializationOperation, false)
        notify(result)
    }

    @Synchronized
    private fun notifyTerm(e: Exception) {
        free()
        log.e(e)
        val result = OperationResult(terminationOperation, false)
        notify(result)
    }

    private fun getInstallationFlow(installer: Installer, dockerInitFlow: InitializationFlow): InstallationFlow {
        val installFlow = InstallationFlow(installer)
        val dieCallback = DieOnFailureCallback<String>()
        softwareConfigurations.forEach {
            installFlow.width(it)
        }
        return installFlow
                .connect(dockerInitFlow)
                .onFinish(dieCallback)
    }

    private fun getDockerFlow(docker: Docker): InstallationFlow {

        val dockerFlow = InstallationFlow(docker)
        containersConfigurations.forEach {
            dockerFlow.width(it)
        }
        dockerFlow.onFinish(TerminationCallback(this))
        return dockerFlow
    }

    private fun getDockerInitFlow(docker: Docker, dockerFlow: InstallationFlow): InitializationFlow {

        val initCallback = DockerInitializationFlowCallback()
        return InitializationFlow()
                .width(docker)
                .connect(dockerFlow)
                .onFinish(initCallback)
    }

    private fun getInitializationFlow(installer: Installer, installFlow: InstallationFlow): InitializationFlow {

        val initCallback = InstallerInitializationFlowCallback()
        return InitializationFlow()
                .width(installer)
                .connect(installFlow)
                .onFinish(initCallback)
    }

    private fun getCommandFlow(ssh: SSH, initFlow: InitializationFlow): CommandFlow {

        val host = ssh.getRemote().host
        val pingCommand = TerminalCommand(Commands.ping(host))
        val hostInfoCommand = TerminalCommand(Commands.getHostInfo())
        val testCommand = TerminalCommand(Commands.echo("Hello"))
        val terminal = ssh.terminal
        val dieCallback = DieOnFailureCallback<String>()
        return CommandFlow()
                .width(terminal)
                .perform(pingCommand)
                .width(ssh)
                .perform(testCommand)
                .perform(hostInfoCommand, HostInfoDataHandler(ssh.getRemoteOS()))
                .onFinish(dieCallback)
                .connect(initFlow)
    }
}