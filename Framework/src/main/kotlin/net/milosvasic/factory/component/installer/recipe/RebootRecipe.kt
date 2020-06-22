package net.milosvasic.factory.component.installer.recipe

import net.milosvasic.factory.component.installer.step.reboot.Reboot
import net.milosvasic.factory.component.installer.step.reboot.RebootOperation
import net.milosvasic.factory.execution.flow.processing.FlowProcessingCallback
import net.milosvasic.factory.operation.OperationResult
import net.milosvasic.factory.operation.OperationResultListener

class RebootRecipe : InstallationStepRecipe() {

    private val operationCallback = object : OperationResultListener {
        override fun onOperationPerformed(result: OperationResult) {
            when (result.operation) {
                is RebootOperation -> {

                    step?.let {
                        (it as Reboot).unsubscribe(this)
                    }
                    callback?.onFinish(result.success)
                    callback = null
                }
            }
        }
    }

    @Throws(IllegalStateException::class, IllegalArgumentException::class)
    override fun process(callback: FlowProcessingCallback) {
        super.process(callback)
        step?.let { s ->
            if (s !is Reboot) {
                throw IllegalArgumentException("Unexpected installation step type: ${s::class.simpleName}")
            }
        }
        try {

            step?.let { s ->
                val step = s as Reboot
                try {
                    executeViaSSH(step, operationCallback)
                } catch (e: IllegalArgumentException) {

                    fail(e)
                }
            }
        } catch (e: IllegalStateException) {

            fail(e)
        } catch (e: IllegalArgumentException) {

            fail(e)
        }
    }
}