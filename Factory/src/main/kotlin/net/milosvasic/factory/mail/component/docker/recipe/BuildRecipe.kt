package net.milosvasic.factory.mail.component.docker.recipe

import net.milosvasic.factory.mail.EMPTY
import net.milosvasic.factory.mail.component.docker.step.dockerfile.Build
import net.milosvasic.factory.mail.component.docker.step.dockerfile.BuildOperation
import net.milosvasic.factory.mail.component.installer.recipe.InstallationStepRecipe
import net.milosvasic.factory.mail.execution.flow.processing.FlowProcessingCallback
import net.milosvasic.factory.mail.log
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.operation.OperationResultListener

class BuildRecipe : InstallationStepRecipe() {

    private val operationCallback = object : OperationResultListener {
        override fun onOperationPerformed(result: OperationResult) {
            when (result.operation) {
                is BuildOperation -> {

                    step?.let { s ->
                        val step = s as Build
                        step.unsubscribe(this)
                    }
                    val errMsg = getErrorMessage(result)
                    callback?.onFinish(result.success, errMsg)
                    callback = null
                }
            }
        }
    }

    @Throws(IllegalStateException::class, IllegalArgumentException::class)
    override fun process(callback: FlowProcessingCallback) {
        super.process(callback)
        step?.let { s ->
            if (s !is Build) {
                throw IllegalArgumentException("Unexpected installation step type: ${s::class.simpleName}")
            }
        }
        try {

            toolkit?.let { tools ->
                step?.let { s ->
                    val step = s as Build
                    tools.connection?.let { conn ->
                        step.subscribe(operationCallback)
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

    override fun getErrorMessage(result: OperationResult) = if (result.success) {
        String.EMPTY
    } else {
        "Could not build Docker image"
    }
}