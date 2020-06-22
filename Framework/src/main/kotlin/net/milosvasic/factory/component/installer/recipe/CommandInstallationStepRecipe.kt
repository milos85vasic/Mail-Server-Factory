package net.milosvasic.factory.component.installer.recipe

import net.milosvasic.factory.component.installer.step.CommandInstallationStep
import net.milosvasic.factory.execution.flow.processing.FlowProcessingCallback
import net.milosvasic.factory.operation.OperationResult
import net.milosvasic.factory.operation.OperationResultListener
import net.milosvasic.factory.terminal.TerminalCommand

class CommandInstallationStepRecipe : InstallationStepRecipe() {

    private var command: TerminalCommand? = null

    private val operationCallback = object : OperationResultListener {
        override fun onOperationPerformed(result: OperationResult) {
            toolkit?.connection?.unsubscribe(this)
            command?.let {
                if (result.operation == it) {
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
            if (s !is CommandInstallationStep) {
                throw IllegalArgumentException("Unexpected installation step type: ${s::class.simpleName}")
            }
        }
        try {

            toolkit?.let { tools ->
                step?.let { s ->
                    val step = s as CommandInstallationStep
                    tools.connection?.let { conn ->
                        command = step.command
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