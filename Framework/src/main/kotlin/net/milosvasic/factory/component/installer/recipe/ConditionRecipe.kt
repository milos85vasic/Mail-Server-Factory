package net.milosvasic.factory.component.installer.recipe

import net.milosvasic.factory.component.installer.step.condition.ConditionOperation
import net.milosvasic.factory.component.installer.step.condition.SkipCondition
import net.milosvasic.factory.component.installer.step.condition.SkipConditionOperation
import net.milosvasic.factory.execution.flow.processing.FlowProcessingCallback
import net.milosvasic.factory.operation.OperationResult
import net.milosvasic.factory.operation.OperationResultListener

class ConditionRecipe : InstallationStepRecipe() {

    private val operationCallback = object : OperationResultListener {
        override fun onOperationPerformed(result: OperationResult) {
            when (result.operation) {
                is SkipConditionOperation -> {
                    step?.let { s ->
                        val step = s as SkipCondition
                        step.unsubscribe(this)
                    }
                    val positive = result.operation is ConditionOperation
                    if (result.success) {
                        if (result.operation.result) {
                            callback?.onFinish(
                                    result.success,
                                    if (positive) {
                                        ConditionRecipeFlowProcessingData(fallThrough = true)
                                    } else {
                                        ConditionRecipeFlowProcessingData(fallThrough = false)
                                    }
                            )
                        } else {
                            callback?.onFinish(
                                    result.success,
                                    if (positive) {
                                        ConditionRecipeFlowProcessingData(fallThrough = false)
                                    } else {
                                        ConditionRecipeFlowProcessingData(fallThrough = true)
                                    }
                            )
                        }
                    } else {
                        if (result.operation.result) {
                            callback?.onFinish(
                                    result.success,
                                    if (positive) {
                                        ConditionRecipeFlowProcessingData(fallThrough = false)
                                    } else {
                                        ConditionRecipeFlowProcessingData(fallThrough = true)
                                    }
                            )
                        } else {
                            callback?.onFinish(false)
                        }
                    }
                    callback = null
                }
            }
        }
    }

    @Throws(IllegalStateException::class, IllegalArgumentException::class)
    override fun process(callback: FlowProcessingCallback) {
        super.process(callback)
        step?.let { s ->
            if (s !is SkipCondition) {
                throw IllegalArgumentException("Unexpected installation step type: ${s::class.simpleName}")
            }
        }
        try {

            toolkit?.let { tools ->
                step?.let { s ->
                    val step = s as SkipCondition
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