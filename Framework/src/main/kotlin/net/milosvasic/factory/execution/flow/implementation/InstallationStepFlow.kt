package net.milosvasic.factory.execution.flow.implementation

import net.milosvasic.factory.common.busy.BusyException
import net.milosvasic.factory.component.Toolkit
import net.milosvasic.factory.component.installer.recipe.ConditionRecipeFlowProcessingData
import net.milosvasic.factory.component.installer.recipe.InstallationStepRecipe
import net.milosvasic.factory.component.installer.step.InstallationStep
import net.milosvasic.factory.execution.flow.FlowBuilder
import net.milosvasic.factory.execution.flow.FlowSimpleBuilder
import net.milosvasic.factory.execution.flow.callback.FlowCallback
import net.milosvasic.factory.execution.flow.processing.FlowProcessingData
import net.milosvasic.factory.execution.flow.processing.ProcessingRecipe
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
    override fun onFinish(callback: FlowCallback): InstallationStepFlow {
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
            if (it != recipe) {
                throw IllegalArgumentException("Recipe for '${clazz.simpleName}' is already registered")
            }
            return this
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

    override fun tryNextSubject(success: Boolean, data: FlowProcessingData?) {
        when (data) {
            is ConditionRecipeFlowProcessingData -> {
                if (success) {
                    if (data.fallThrough) {
                        super.tryNextSubject(success, data)
                    } else {
                        finish(true)
                    }
                } else {
                    if (data.fallThrough) {
                        super.tryNextSubject(true, data)
                    } else {
                        finish(true)
                    }
                }
            }
            else -> {
                super.tryNextSubject(success, data)
            }
        }
    }
}