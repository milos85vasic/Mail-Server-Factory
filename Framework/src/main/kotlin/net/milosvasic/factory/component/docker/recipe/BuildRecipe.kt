package net.milosvasic.factory.component.docker.recipe

import net.milosvasic.factory.component.docker.step.dockerfile.Build
import net.milosvasic.factory.component.docker.step.dockerfile.BuildOperation
import net.milosvasic.factory.component.installer.recipe.InstallationStepRecipe
import net.milosvasic.factory.execution.flow.processing.FlowProcessingCallback
import net.milosvasic.factory.log
import net.milosvasic.factory.operation.OperationResult
import net.milosvasic.factory.operation.OperationResultListener

class BuildRecipe : InstallationStepRecipe() {

    private val operationCallback = object : OperationResultListener {
        override fun onOperationPerformed(result: OperationResult) {
            when (result.operation) {
                is BuildOperation -> {

                    step?.let { s ->
                        val step = s as Build
                        step.unsubscribe(this)
                    }
                    if (!result.success) {
                        log.e("Could not build Docker image")
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
}