package net.milosvasic.factory.mail.execution.flow.implementation

import net.milosvasic.factory.mail.common.busy.BusyException
import net.milosvasic.factory.mail.component.Toolkit
import net.milosvasic.factory.mail.component.installer.recipe.InstallationStepRecipe
import net.milosvasic.factory.mail.component.installer.step.InstallationStep
import net.milosvasic.factory.mail.execution.flow.FlowBuilder
import net.milosvasic.factory.mail.execution.flow.FlowSimpleBuilder
import net.milosvasic.factory.mail.execution.flow.callback.FlowCallback
import net.milosvasic.factory.mail.execution.flow.processing.ProcessingRecipe
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

class InstallationStepFlow(private val toolkit: Toolkit) : FlowSimpleBuilder<InstallationStep<*>, String>() {

    private val recipes = mutableMapOf<KClass<*>, KClass<*>>()

    @Throws(BusyException::class)
    override fun width(subject: InstallationStep<*>): InstallationStepFlow {
        super.width(subject)
        return this
    }

    @Throws(BusyException::class)
    override fun onFinish(callback: FlowCallback<String>): InstallationStepFlow {
        super.onFinish(callback)
        return this
    }

    @Throws(BusyException::class)
    override fun connect(flow: FlowBuilder<*, *, *>): InstallationStepFlow {
        super.connect(flow)
        return this
    }

    @Throws(IllegalArgumentException::class)
    fun <STEP : InstallationStep<*>, RECIPE : ProcessingRecipe> registerRecipe(
            clazz: KClass<STEP>, recipe: KClass<RECIPE>
    ): InstallationStepFlow {

        val existing = recipes[clazz]
        existing?.let {
            throw IllegalArgumentException("Recipe for '${clazz.simpleName}' is already registered")
        }
        recipes[clazz] = recipe
        return this
    }

    @Throws(IllegalArgumentException::class)
    override fun getProcessingRecipe(subject: InstallationStep<*>): ProcessingRecipe {
        val recipe = recipes[subject::class]
        recipe?.let {
            val instance = it.createInstance() as InstallationStepRecipe
            instance.toolkit(toolkit)
            instance.installationStep(subject)
            return instance
        }
        throw IllegalArgumentException("No processing recipe available for: ${subject::class.simpleName}")
    }
}