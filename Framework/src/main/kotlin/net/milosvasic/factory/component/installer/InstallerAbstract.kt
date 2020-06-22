package net.milosvasic.factory.component.installer

import net.milosvasic.factory.common.busy.BusyException
import net.milosvasic.factory.common.busy.BusyWorker
import net.milosvasic.factory.common.initialization.Initializer
import net.milosvasic.factory.common.initialization.Termination
import net.milosvasic.factory.component.Toolkit
import net.milosvasic.factory.component.installer.recipe.registration.MainRecipeRegistrar
import net.milosvasic.factory.component.installer.step.InstallationStep
import net.milosvasic.factory.configuration.ConfigurableSoftware
import net.milosvasic.factory.configuration.SoftwareConfiguration
import net.milosvasic.factory.execution.flow.callback.FlowCallback
import net.milosvasic.factory.execution.flow.implementation.InstallationStepFlow
import net.milosvasic.factory.execution.flow.processing.ProcessingRecipesRegistration
import net.milosvasic.factory.log
import net.milosvasic.factory.operation.Operation
import net.milosvasic.factory.operation.OperationResult
import net.milosvasic.factory.remote.Connection

abstract class InstallerAbstract(entryPoint: Connection) :
        BusyWorker<InstallationStep<*>>(entryPoint),
        Initializer,
        Termination,
        ConfigurableSoftware,
        Installation,
        ProcessingRecipesRegistration {

    private var config: SoftwareConfiguration? = null
    private val mainRecipeRegistrar = MainRecipeRegistrar()
    private lateinit var steps: Map<String, List<InstallationStep<*>>>
    protected val recipeRegistrars = mutableListOf<ProcessingRecipesRegistration>(mainRecipeRegistrar)

    private val flowCallback = object : FlowCallback {

        override fun onFinish(success: Boolean) {
            free(success)
        }
    }

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

    @Throws(IllegalArgumentException::class)
    override fun registerRecipes(step: InstallationStep<*>, flow: InstallationStepFlow): Boolean {

        recipeRegistrars.forEach { registrar ->
            if (registrar.registerRecipes(step, flow)) {
                return true
            }
        }
        return false
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

    fun addProcessingRecipesRegistrar(registrar: ProcessingRecipesRegistration) {
        if (!recipeRegistrars.contains(registrar)) {
            recipeRegistrars.add(registrar)
        }
    }

    @Throws(IllegalStateException::class)
    protected abstract fun initialization()

    @Throws(IllegalStateException::class)
    protected abstract fun termination()

    protected abstract fun getEnvironmentName(): String

    protected abstract fun getNotifyOperation(): Operation

    protected abstract fun getToolkit(): Toolkit
}