package net.milosvasic.factory.component.installer.recipe

import net.milosvasic.factory.component.installer.step.deploy.Deploy
import net.milosvasic.factory.component.installer.step.deploy.DeployOperation
import net.milosvasic.factory.execution.flow.processing.FlowProcessingCallback
import net.milosvasic.factory.operation.OperationResult
import net.milosvasic.factory.operation.OperationResultListener

class DeployRecipe : InstallationStepRecipe() {

    private val operationCallback = object : OperationResultListener {
        override fun onOperationPerformed(result: OperationResult) {
            when (result.operation) {
                is DeployOperation -> {

                    step?.let {
                        (it as Deploy).unsubscribe(this)
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
            if (s !is Deploy) {
                throw IllegalArgumentException("Unexpected installation step type: ${s::class.simpleName}")
            }
        }
        try {

            step?.let { s ->
                val step = s as Deploy
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