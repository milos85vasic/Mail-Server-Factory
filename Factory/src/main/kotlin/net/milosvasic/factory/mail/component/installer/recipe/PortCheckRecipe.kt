package net.milosvasic.factory.mail.component.installer.recipe

import net.milosvasic.factory.mail.component.installer.step.port.PortCheck
import net.milosvasic.factory.mail.component.installer.step.port.PortCheckOperation
import net.milosvasic.factory.mail.execution.flow.processing.FlowProcessingCallback
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.operation.OperationResultListener

class PortCheckRecipe : InstallationStepRecipe() {

    private val operationCallback = object : OperationResultListener {
        override fun onOperationPerformed(result: OperationResult) {
            when (result.operation) {
                is PortCheckOperation -> {

                    step?.let {
                        (it as PortCheck).unsubscribe(this)
                    }
                    callback?.onFinish(result.success, getErrorMessage(result))
                    callback = null
                }
            }
        }
    }

    @Throws(IllegalStateException::class, IllegalArgumentException::class)
    override fun process(callback: FlowProcessingCallback) {
        super.process(callback)
        step?.let { s ->
            if (s !is PortCheck) {
                throw IllegalArgumentException("Unexpected installation step type: ${s::class.simpleName}")
            }
        }
        try {

            step?.let { s ->
                val step = s as PortCheck
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