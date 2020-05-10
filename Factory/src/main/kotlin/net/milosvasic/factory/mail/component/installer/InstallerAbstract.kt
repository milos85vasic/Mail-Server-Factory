package net.milosvasic.factory.mail.component.installer

import net.milosvasic.factory.mail.EMPTY
import net.milosvasic.factory.mail.common.busy.BusyException
import net.milosvasic.factory.mail.common.busy.LegacyBusyWorker
import net.milosvasic.factory.mail.common.initialization.Initialization
import net.milosvasic.factory.mail.component.docker.step.stack.CheckOperation
import net.milosvasic.factory.mail.component.installer.step.CommandInstallationStep
import net.milosvasic.factory.mail.component.installer.step.InstallationStep
import net.milosvasic.factory.mail.component.installer.step.RemoteOperationInstallationStep
import net.milosvasic.factory.mail.component.installer.step.condition.Condition
import net.milosvasic.factory.mail.component.installer.step.condition.ConditionOperation
import net.milosvasic.factory.mail.component.installer.step.deploy.Deploy
import net.milosvasic.factory.mail.component.installer.step.deploy.DeployOperation
import net.milosvasic.factory.mail.component.installer.step.reboot.Reboot
import net.milosvasic.factory.mail.component.installer.step.reboot.RebootOperation
import net.milosvasic.factory.mail.component.packaging.PackageInstallerInitializationOperation
import net.milosvasic.factory.mail.component.packaging.PackageManagerOperation
import net.milosvasic.factory.mail.configuration.ConfigurableSoftware
import net.milosvasic.factory.mail.configuration.SoftwareConfiguration
import net.milosvasic.factory.mail.log
import net.milosvasic.factory.mail.operation.Operation
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.operation.OperationResultListener
import net.milosvasic.factory.mail.remote.Connection
import net.milosvasic.factory.mail.remote.ssh.SSH
import net.milosvasic.factory.mail.terminal.TerminalCommand

abstract class InstallerAbstract(entryPoint: Connection) :
        LegacyBusyWorker<InstallationStep<*>>(entryPoint),
        ConfigurableSoftware,
        Installation {

    protected var item: InstallationStep<*>? = null

    private var config: SoftwareConfiguration? = null
    private var sectionIterator: Iterator<String>? = null
    private lateinit var steps: Map<String, List<InstallationStep<*>>>

    private val listener = object : OperationResultListener {
        override fun onOperationPerformed(result: OperationResult) {
            handleResultAndCatch(result)
        }
    }

    @Throws(IllegalStateException::class, IllegalArgumentException::class)
    override fun handleResult(result: OperationResult) {
        when (result.operation) {
            is PackageInstallerInitializationOperation -> {

                free()
                val installerInitializationOperation = InstallerInitializationOperation()
                val operationResult = OperationResult(installerInitializationOperation, result.success)
                notify(operationResult)
            }
            is PackageManagerOperation -> {

                checkResultAndTryNext(result)
            }
            is RebootOperation -> {

                unsubscribeFromItem(listener)
                checkResultAndTryNext(result)
            }
            is CheckOperation -> {

                unsubscribeFromItem(listener)
                checkResultAndTryNext(result)
            }
            is ConditionOperation -> {

                unsubscribeFromItem(listener)
                if (result.success) {
                    if (result.operation.result) {
                        tryNextSection()
                    } else {
                        tryNext()
                    }
                } else {
                    if (result.operation.result) {
                        tryNext()
                    } else {
                        free(false)
                    }
                }
            }
            is DeployOperation -> {

                unsubscribeFromItem(listener)
                if (result.success) {
                    tryNext()
                } else {
                    free(false)
                }
            }
            is TerminalCommand -> {

                if (command != String.EMPTY && result.operation.command.endsWith(command)) {
                    onCommandPerformed(result)
                }
            }
        }
    }

    protected fun checkResultAndTryNext(result: OperationResult) {
        if (result.success) {
            tryNextOrFail()
        } else {
            free(false)
        }
    }

    protected open fun onCommandPerformed(result: OperationResult) {
        checkResultAndTryNext(result)
    }

    @Synchronized
    override fun install() {

        if (config == null) {

            log.e("No configuration available. Please set configuration before installation")
            free(false)
            return
        } else {

            config?.let {
                try {
                    steps = it.obtain(getEnvironmentName())
                    busy()
                    sectionIterator = steps.keys.iterator()
                    sectionIterator?.let { secIt ->

                        if (secIt.hasNext()) {
                            iterator = steps[secIt.next()]?.iterator()
                            tryNext()
                        } else {

                            log.e("No section to iterate")
                            free(false)
                            return
                        }
                    }
                } catch (e: IllegalArgumentException) {

                    onFailedResult(e)
                } catch (e: IllegalStateException) {

                    onFailedResult(e)
                }
            }
        }
    }

    @Synchronized
    @Throws(UnsupportedOperationException::class)
    override fun uninstall() {
        throw UnsupportedOperationException("Not implemented yet")
    }

    @Throws(IllegalStateException::class, IllegalArgumentException::class)
    override fun tryNext() {

        if (iterator == null) {
            free(false)
            return
        }
        iterator?.let {
            if (it.hasNext()) {
                item = it.next()
                item?.let { current ->
                    handleNext(current)
                }
            } else {

                tryNextSection()
            }
        }
    }

    @Throws(IllegalStateException::class, IllegalArgumentException::class)
    protected open fun handleNext(current: InstallationStep<*>): Boolean {
        when (current) {
            is CommandInstallationStep -> {

                command = current.command
                current.execute(entryPoint)
                return true
            }
            is Condition -> {

                command = String.EMPTY
                current.subscribe(listener)
                current.execute(entryPoint)
                return true
            }
            is Reboot -> {

                executeViaSSH(current)
                return true
            }
            is Deploy -> {

                executeViaSSH(current)
                return true
            }
        }
        return false
    }

    @Synchronized
    @Throws(IllegalStateException::class)
    override fun initialize() {
        checkInitialized()
        busy()
    }

    @Synchronized
    @Throws(IllegalStateException::class)
    override fun terminate() {
        checkNotInitialized()
        clearConfiguration()
        super.terminate()
    }

    @Synchronized
    @Throws(IllegalStateException::class)
    override fun checkInitialized() {
        if (isInitialized()) {
            throw IllegalStateException("Installer has been already initialized")
        }
    }

    @Synchronized
    @Throws(IllegalStateException::class)
    override fun checkNotInitialized() {
        if (!isInitialized()) {
            throw IllegalStateException("Installer has not been initialized")
        }
    }

    @Synchronized
    @Throws(BusyException::class)
    override fun setConfiguration(configuration: SoftwareConfiguration) {
        busy()
        this.config = configuration
        free()
    }

    @Synchronized
    @Throws(BusyException::class)
    override fun clearConfiguration() {
        busy()
        config = null
        free()
    }

    @Throws(IllegalStateException::class, IllegalArgumentException::class)
    override fun onSuccessResult() {
        tryNext()
    }

    override fun onFailedResult() {
        free(false)
    }

    @Synchronized
    override fun notify(success: Boolean) {
        val operation = getNotifyOperation()
        val result = OperationResult(operation, success)
        notify(result)
    }

    abstract fun getEnvironmentName(): String

    abstract fun getNotifyOperation(): Operation

    protected fun unsubscribeFromItem(listener: OperationResultListener) {
        item?.let { current ->
            when (current) {
                is RemoteOperationInstallationStep -> {
                    current.unsubscribe(listener)
                }
            }
        }
    }

    protected fun handleResultAndCatch(result: OperationResult) {
        try {
            handleResult(result)
        } catch (e: IllegalStateException) {
            onFailedResult(e)
        } catch (e: IllegalArgumentException) {
            onFailedResult(e)
        }
    }

    @Throws(IllegalStateException::class, IllegalArgumentException::class)
    private fun tryNextSection() {
        sectionIterator?.let { secIt ->

            if (secIt.hasNext()) {
                iterator = steps[secIt.next()]?.iterator()
                tryNext()
            } else {
                free(true)
            }
        }
    }

    private fun tryNextOrFail() {
        try {
            tryNext()
        } catch (e: IllegalStateException) {
            onFailedResult(e)
        } catch (e: IllegalArgumentException) {
            onFailedResult(e)
        }
    }

    private fun executeViaSSH(step: RemoteOperationInstallationStep<SSH>) {
        if (entryPoint is SSH) {

            command = String.EMPTY
            step.subscribe(listener)
            step.execute(entryPoint)
        } else {

            val clazz = entryPoint::class.simpleName
            val msg = "${step::class.simpleName} installation step does not support $clazz connection"
            throw IllegalArgumentException(msg)
        }
    }
}