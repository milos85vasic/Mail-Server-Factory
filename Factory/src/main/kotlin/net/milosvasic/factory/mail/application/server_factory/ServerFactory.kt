package net.milosvasic.factory.mail.application.server_factory

import net.milosvasic.factory.mail.EMPTY
import net.milosvasic.factory.mail.application.ArgumentsValidator
import net.milosvasic.factory.mail.common.Application
import net.milosvasic.factory.mail.common.busy.Busy
import net.milosvasic.factory.mail.common.busy.BusyDelegation
import net.milosvasic.factory.mail.common.busy.BusyException
import net.milosvasic.factory.mail.common.busy.BusyWorker
import net.milosvasic.factory.mail.common.exception.EmptyDataException
import net.milosvasic.factory.mail.component.docker.Docker
import net.milosvasic.factory.mail.component.docker.DockerInitializationOperation
import net.milosvasic.factory.mail.component.docker.DockerOperation
import net.milosvasic.factory.mail.component.installer.Installer
import net.milosvasic.factory.mail.component.installer.InstallerAbstract
import net.milosvasic.factory.mail.component.installer.InstallerInitializationOperation
import net.milosvasic.factory.mail.component.installer.InstallerOperation
import net.milosvasic.factory.mail.configuration.*
import net.milosvasic.factory.mail.error.ERROR
import net.milosvasic.factory.mail.execution.flow.callback.FlowCallback
import net.milosvasic.factory.mail.execution.flow.implementation.CommandFlow
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
        configuration = null
        softwareConfigurations.clear()
        containersConfigurations.clear()
        notifyTerm(true)
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
        log.i("STARTED")
        try {
            configuration?.let { config ->

                val host = config.remote.host
                val ssh = SSH(config.remote)
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
                            onStop()
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

                val flowCallback = object : FlowCallback<String> {
                    override fun onFinish(success: Boolean, message: String, data: String?) {

                        if (success) {
                            ssh.subscribe(listener)
                            installer.subscribe(listener)
                            installer.initialize()
                        } else {
                            log.e(message)
                            fail(ERROR.INITIALIZATION_FAILURE)
                        }
                    }
                }

                CommandFlow()
                        .width(terminal)
                        .perform(pingCommand)
                        .width(ssh)
                        .perform(testCommand)
                        .perform(
                                hostInfoCommand,
                                HostInfoDataHandler(ssh.getRemoteOS())
                        )
                        .onFinish(flowCallback)
                        .run()
            }
        } catch (e: IllegalArgumentException) {

            fail(e)
        } catch (e: IllegalStateException) {

            fail(e)
        }
    }

    override fun onStop() {
        log.i("FINISHED")
        try {
            terminate()
        } catch (e: IllegalStateException) {

            log.e(e)
        }
    }

    @Synchronized
    @Throws(BusyException::class)
    override fun busy() {
        BusyWorker.busy(busy)
    }

    @Synchronized
    override fun free() {
        BusyWorker.free(busy)
    }

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

    private fun notifyTerm(success: Boolean) {
        free()
        val result = OperationResult(terminationOperation, success)
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
}