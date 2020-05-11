package net.milosvasic.factory.mail.execution.flow.implementation

import net.milosvasic.factory.mail.common.busy.BusyException
import net.milosvasic.factory.mail.component.installer.step.InstallationStep
import net.milosvasic.factory.mail.execution.flow.FlowBuilder
import net.milosvasic.factory.mail.execution.flow.FlowSimpleBuilder
import net.milosvasic.factory.mail.execution.flow.callback.FlowCallback
import net.milosvasic.factory.mail.execution.flow.processing.ProcessingRecipe
import net.milosvasic.factory.mail.remote.Connection
import kotlin.reflect.KClass

class InstallationStepFlow(private val entryPoint: Connection) : FlowSimpleBuilder<InstallationStep<*>, String>() {

    private val recipes = mutableMapOf<KClass<InstallationStep<*>>, ProcessingRecipe>()

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
    fun registerRecipe(clazz: KClass<InstallationStep<*>>, recipe: ProcessingRecipe): InstallationStepFlow {
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
            return recipe
        }
        throw IllegalArgumentException("No processing recipe available for: ${subject::class.simpleName}")
    }
}