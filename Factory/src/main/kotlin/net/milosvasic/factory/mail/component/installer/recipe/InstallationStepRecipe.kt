package net.milosvasic.factory.mail.component.installer.recipe

import net.milosvasic.factory.mail.EMPTY
import net.milosvasic.factory.mail.component.Toolkit
import net.milosvasic.factory.mail.component.installer.step.InstallationStep
import net.milosvasic.factory.mail.execution.flow.processing.FlowProcessingCallback
import net.milosvasic.factory.mail.execution.flow.processing.ProcessingRecipe
import net.milosvasic.factory.mail.getMessage
import net.milosvasic.factory.mail.operation.OperationResult

abstract class InstallationStepRecipe : ProcessingRecipe {

    protected var toolkit: Toolkit? = null
    protected var step: InstallationStep<*>? = null
    protected var callback: FlowProcessingCallback? = null

    @Throws(IllegalArgumentException::class)
    override fun process(callback: FlowProcessingCallback) {
        this.callback = callback
        val validator = InstallationStepRecipeValidator()
        if (!validator.validate(this)) {
            throw IllegalArgumentException("Invalid installation step recipe: $this")
        }
        if (toolkit?.connection == null) {
            throw IllegalArgumentException("Connection not provided")
        }
    }

    protected fun fail(e: Exception) {
        callback?.onFinish(false, e.getMessage())
    }

    protected fun getErrorMessage(result: OperationResult) = if (result.success) {
        String.EMPTY
    } else {
        "Installation step failed: $step"
    }

    @Throws(IllegalStateException::class)
    fun toolkit(toolkit: Toolkit) {
        this.toolkit?.let {
            throw IllegalStateException("Toolkit point is already set")
        }
        this.toolkit = toolkit
    }

    @Throws(IllegalStateException::class)
    fun installationStep(step: InstallationStep<*>) {
        this.step?.let {
            throw IllegalStateException("Installation step is already set")
        }
        this.step = step
    }

    fun obtainToolkit(): Toolkit? = toolkit

    fun obtainInstallationStep(): InstallationStep<*>? = step
}