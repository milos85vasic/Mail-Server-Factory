package net.milosvasic.factory.mail.execution.flow.command

import net.milosvasic.factory.mail.EMPTY
import net.milosvasic.factory.mail.common.Executor
import net.milosvasic.factory.mail.common.busy.BusyException
import net.milosvasic.factory.mail.execution.flow.FlowBuilder
import net.milosvasic.factory.mail.execution.flow.FlowCallback
import net.milosvasic.factory.mail.execution.flow.FlowProcessingCallback
import net.milosvasic.factory.mail.execution.flow.ProcessingRecipe
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.operation.OperationResultListener
import net.milosvasic.factory.mail.terminal.TerminalCommand

class CommandFlow : FlowBuilder<Executor<TerminalCommand>, TerminalCommand>() {

    @Throws(BusyException::class)
    override fun width(subject: Executor<TerminalCommand>): CommandFlow {
        super.width(subject)
        return this
    }

    @Throws(BusyException::class)
    fun perform(what: String): CommandFlow {
        perform(TerminalCommand(what))
        return this
    }

    @Throws(BusyException::class)
    override fun perform(what: TerminalCommand): CommandFlow {
        super.perform(what)
        return this
    }

    @Throws(BusyException::class)
    override fun onFinish(callback: FlowCallback): CommandFlow {
        super.onFinish(callback)
        return this
    }

    override fun getProcessingRecipe(
            subject: Executor<TerminalCommand>,
            operation: TerminalCommand
    ): ProcessingRecipe {

        return object : ProcessingRecipe {

            private var callback: FlowProcessingCallback? = null

            private val operationCallback = object : OperationResultListener {
                override fun onOperationPerformed(result: OperationResult) {
                    subject.unsubscribe(this)
                    val message = if (result.success) {
                        String.EMPTY
                    } else {
                        if (result.operation is TerminalCommand) {
                            "Command failed: ${result.operation.command}"
                        } else {
                            "Command failed: ${result.operation}"
                        }
                    }
                    callback?.onFinish(result.success, message)
                    callback = null
                }
            }

            override fun process(callback: FlowProcessingCallback) {
                this.callback = callback
                subject.subscribe(operationCallback)
                subject.execute(operation)
            }
        }
    }
}