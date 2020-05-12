package net.milosvasic.factory.mail.component.installer.recipe

import net.milosvasic.factory.mail.EMPTY
import net.milosvasic.factory.mail.common.Subscription
import net.milosvasic.factory.mail.component.installer.step.InstallationStep
import net.milosvasic.factory.mail.execution.flow.processing.FlowProcessingCallback
import net.milosvasic.factory.mail.execution.flow.processing.ProcessingRecipe
import net.milosvasic.factory.mail.getMessage
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.operation.OperationResultListener

abstract class InstallationStepRecipe<T : Subscription<OperationResultListener>> : ProcessingRecipe {

    protected var entryPoint: T? = null
    protected var step: InstallationStep<*>? = null
    protected var callback: FlowProcessingCallback? = null

    override fun process(callback: FlowProcessingCallback) {
        this.callback = callback

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
    fun entryPoint(entryPoint: T) {
        this.entryPoint?.let {
            throw IllegalStateException("Entry point is already set")
        }
        this.entryPoint = entryPoint
    }

    @Throws(IllegalStateException::class)
    fun installationStep(step: InstallationStep<*>) {
        this.step?.let {
            throw IllegalStateException("Installation step is already set")
        }
        this.step = step
    }

    fun obtainEntryPoint(): T? = entryPoint

    fun obtainInstallationStep(): InstallationStep<*>? = step
}