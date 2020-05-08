package net.milosvasic.factory.mail.application

import net.milosvasic.factory.mail.EMPTY
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
import kotlin.system.exitProcess

class ServerFactory : Application, BusyDelegation {

    private val busy = Busy()
    private val arguments = mutableListOf<String>()
    private val subscribers = ConcurrentLinkedQueue<OperationResultListener>()

    override fun initialize() {
        checkInitialized()
        busy()

        free()
    }

    override fun terminate() {
        checkNotInitialized()
        busy()

        free()
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

    override fun run(args: Array<String>) {
        log.i("STARTED")
        arguments.clear()
        arguments.addAll(args)

        // TODO: If initialized skip, if not initialize.

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

    private fun finish() {
        onStop()
    }

    override fun onStop() {
        log.i("FINISHED")
        terminate()
        exitProcess(0)
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
            throw IllegalStateException("Configuration manager has been already initialized")
        }
    }

    @Synchronized
    @Throws(IllegalStateException::class)
    override fun checkNotInitialized() {
        if (!ConfigurationManager.isInitialized()) {
            throw IllegalStateException("Configuration manager has not been initialized")
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
}