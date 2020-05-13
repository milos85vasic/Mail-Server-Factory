package net.milosvasic.factory.mail.component.installer

import net.milosvasic.factory.mail.common.busy.BusyException
import net.milosvasic.factory.mail.common.busy.BusyWorker
import net.milosvasic.factory.mail.common.initialization.Initializer
import net.milosvasic.factory.mail.common.initialization.Termination
import net.milosvasic.factory.mail.component.Toolkit
import net.milosvasic.factory.mail.component.installer.recipe.CommandInstallationStepRecipe
import net.milosvasic.factory.mail.component.installer.recipe.ConditionRecipe
import net.milosvasic.factory.mail.component.installer.recipe.DeployRecipe
import net.milosvasic.factory.mail.component.installer.step.CommandInstallationStep
import net.milosvasic.factory.mail.component.installer.step.InstallationStep
import net.milosvasic.factory.mail.component.installer.step.condition.Condition
import net.milosvasic.factory.mail.component.installer.step.deploy.Deploy
import net.milosvasic.factory.mail.configuration.ConfigurableSoftware
import net.milosvasic.factory.mail.configuration.SoftwareConfiguration
import net.milosvasic.factory.mail.execution.flow.callback.FlowCallback
import net.milosvasic.factory.mail.execution.flow.implementation.InstallationStepFlow
import net.milosvasic.factory.mail.log
import net.milosvasic.factory.mail.operation.Operation
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.remote.Connection

abstract class InstallerAbstract(entryPoint: Connection) :
        BusyWorker<InstallationStep<*>>(entryPoint),
        Initializer,
        Termination,
        ConfigurableSoftware,
        Installation {

    private var config: SoftwareConfiguration? = null
    private lateinit var steps: Map<String, List<InstallationStep<*>>>

    private val flowCallback = object : FlowCallback<String> {

        override fun onFinish(success: Boolean, message: String, data: String?) {
            if (!success) {
                log.e(message)
            }
            free(success)
        }
    }

    // TODO: Goes into recipes - Start
    /*
    private val listener = object : OperationResultListener {
        override fun onOperationPerformed(result: OperationResult) {
            handleResultAndCatch(result)
        }
    }

    @Throws(IllegalStateException::class, IllegalArgumentException::class)
    fun handleResult(result: OperationResult) {
        when (result.operation) {

            is RebootOperation -> {

                unsubscribeFromItem(listener)
                //checkResultAndTryNext(result)
            }
            is CheckOperation -> {

                unsubscribeFromItem(listener)
                //checkResultAndTryNext(result)
            }
        }
    }

    @Throws(IllegalStateException::class, IllegalArgumentException::class)
    protected open fun handleNext(current: InstallationStep<*>): Boolean {
        when (current) {
            is Reboot -> {

                executeViaSSH(current)
                return true
            }
        }
        return false
    }
    */
    // TODO: Goes into recipes - End

    @Synchronized
    override fun install() {

        if (config == null) {

            log.e("No configuration available. Please set configuration before installation")
            free(false)
            return
        } else {

            config?.let { softwareConfiguration ->
                try {
                    steps = softwareConfiguration.obtain(getEnvironmentName())
                    busy()
                    val flow = InstallationStepFlow(getToolkit())
                    steps.keys.forEach { key ->
                        val values = steps[key]
                        values?.forEach { step ->
                            flow.width(step)
                            registerRecipes(step, flow)
                        }
                    }
                    flow.onFinish(flowCallback).run()
                } catch (e: IllegalArgumentException) {

                    onFailedResult(e)
                } catch (e: IllegalStateException) {

                    onFailedResult(e)
                }
            }
        }
    }

    protected open fun registerRecipes(step: InstallationStep<*>, flow: InstallationStepFlow) {
        when (step) {
            is CommandInstallationStep -> {
                flow.registerRecipe(
                        CommandInstallationStep::class,
                        CommandInstallationStepRecipe::class
                )
            }
            is Condition -> {
                flow.registerRecipe(
                        Condition::class,
                        ConditionRecipe::class
                )
            }
            is Deploy -> {
                flow.registerRecipe(
                        Deploy::class,
                        DeployRecipe::class
                )
            }
        }
    }

    @Synchronized
    @Throws(UnsupportedOperationException::class)
    override fun uninstall() {
        throw UnsupportedOperationException("Not implemented yet")
    }

    @Synchronized
    @Throws(IllegalStateException::class)
    final override fun initialize() {
        checkInitialized()
        busy()
        initialization()
    }

    @Synchronized
    @Throws(IllegalStateException::class)
    final override fun terminate() {
        checkNotInitialized()
        clearConfiguration()
        log.v("Shutting down: $this")
        termination()
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

    override fun onSuccessResult() {
        free(true)
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

    @Throws(IllegalStateException::class)
    protected abstract fun initialization()

    @Throws(IllegalStateException::class)
    protected abstract fun termination()

    protected abstract fun getEnvironmentName(): String

    protected abstract fun getNotifyOperation(): Operation

    protected abstract fun getToolkit(): Toolkit
}