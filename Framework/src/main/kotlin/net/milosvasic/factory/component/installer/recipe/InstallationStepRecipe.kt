package net.milosvasic.factory.component.installer.recipe

import net.milosvasic.factory.component.Toolkit
import net.milosvasic.factory.component.installer.step.InstallationStep
import net.milosvasic.factory.component.installer.step.RemoteOperationInstallationStep
import net.milosvasic.factory.execution.flow.processing.FlowProcessingCallback
import net.milosvasic.factory.execution.flow.processing.ProcessingRecipe
import net.milosvasic.factory.log
import net.milosvasic.factory.operation.OperationResultListener
import net.milosvasic.factory.remote.ssh.SSH

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

    protected fun fail(e: Exception) {

        log.e(e)
        callback?.onFinish(false)
    }

    @Throws(IllegalArgumentException::class)
    protected fun executeViaSSH(
            step: RemoteOperationInstallationStep<SSH>,
            listener: OperationResultListener
    ) {
        if (toolkit?.connection == null) {
            throw IllegalArgumentException("Connection is null")
        }
        toolkit?.connection?.let {conn ->
            if (conn is SSH) {
                step.subscribe(listener)
                step.execute(conn)
            } else {

                val clazz = conn::class.simpleName
                val msg = "${step::class.simpleName} installation step does not support $clazz connection"
                throw IllegalArgumentException(msg)
            }
        }
    }
}