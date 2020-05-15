package net.milosvasic.factory.mail.component.docker.recipe

import net.milosvasic.factory.mail.component.docker.step.stack.CheckOperation
import net.milosvasic.factory.mail.component.docker.step.stack.SkipConditionCheck
import net.milosvasic.factory.mail.component.installer.recipe.InstallationStepRecipe
import net.milosvasic.factory.mail.execution.flow.processing.FlowProcessingCallback
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.operation.OperationResultListener

class CheckRecipe : InstallationStepRecipe() {

    private val operationCallback = object : OperationResultListener {
        override fun onOperationPerformed(result: OperationResult) {
            toolkit?.connection?.unsubscribe(this)
            when (result.operation) {
                is CheckOperation -> {

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
            if (s !is SkipConditionCheck) {
                throw IllegalArgumentException("Unexpected installation step type: ${s::class.simpleName}")
            }
        }
        try {

            toolkit?.let { tools ->
                step?.let { s ->
                    val step = s as SkipConditionCheck
                    tools.connection?.let { conn ->
                        conn.subscribe(operationCallback)
                        step.execute(conn)
                    }
                }
            }
        } catch (e: IllegalStateException) {

            fail(e)
        } catch (e: IllegalArgumentException) {

            fail(e)
        }
    }
}