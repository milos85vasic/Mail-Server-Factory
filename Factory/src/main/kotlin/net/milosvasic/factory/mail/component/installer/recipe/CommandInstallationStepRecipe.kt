package net.milosvasic.factory.mail.component.installer.recipe

import net.milosvasic.factory.mail.EMPTY
import net.milosvasic.factory.mail.component.installer.step.CommandInstallationStep
import net.milosvasic.factory.mail.execution.flow.processing.FlowProcessingCallback
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.operation.OperationResultListener
import net.milosvasic.factory.mail.terminal.TerminalCommand

class CommandInstallationStepRecipe : InstallationStepRecipe() {

    private var command = String.EMPTY

    private val operationCallback = object : OperationResultListener {
        override fun onOperationPerformed(result: OperationResult) {
            entryPoint?.unsubscribe(this)
            when (result.operation) {
                is TerminalCommand -> {

                    val resultCommand = result.operation.command
                    if (command != String.EMPTY && resultCommand.endsWith(command)) {

                        callback?.onFinish(result.success, getErrorMessage(result))
                        callback = null
                    }
                }
            }
        }
    }

    @Throws(IllegalStateException::class, IllegalArgumentException::class)
    override fun process(callback: FlowProcessingCallback) {
        super.process(callback)
        if (entryPoint == null) {
            throw IllegalStateException("Entry point is not provided")
        }
        if (step == null) {
            throw IllegalStateException("Installation step is not provided")
        }
        step?.let { s ->
            if (s !is CommandInstallationStep) {
                throw IllegalArgumentException("Unexpected installation step type: ${s::class.simpleName}")
            }
        }
        entryPoint?.subscribe(operationCallback)
        try {

            entryPoint?.let { entry ->
                step?.let { s ->
                    command = (s as CommandInstallationStep).command
                    s.execute(entry)
                }
            }
        } catch (e: IllegalStateException) {

            fail(e)
        } catch (e: IllegalArgumentException) {

            fail(e)
        }
    }
}