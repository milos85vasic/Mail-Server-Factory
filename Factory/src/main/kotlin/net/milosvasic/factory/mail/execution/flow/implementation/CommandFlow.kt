package net.milosvasic.factory.mail.execution.flow.implementation

import net.milosvasic.factory.mail.EMPTY
import net.milosvasic.factory.mail.common.DataHandler
import net.milosvasic.factory.mail.common.Wrapper
import net.milosvasic.factory.mail.common.busy.BusyException
import net.milosvasic.factory.mail.common.execution.Executor
import net.milosvasic.factory.mail.common.obtain.Obtain
import net.milosvasic.factory.mail.execution.flow.FlowBuilder
import net.milosvasic.factory.mail.execution.flow.FlowPerformBuilder
import net.milosvasic.factory.mail.execution.flow.callback.FlowCallback
import net.milosvasic.factory.mail.execution.flow.processing.FlowProcessingCallback
import net.milosvasic.factory.mail.execution.flow.processing.ProcessingRecipe
import net.milosvasic.factory.mail.getMessage
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.operation.OperationResultListener
import net.milosvasic.factory.mail.operation.command.CommandConfiguration
import net.milosvasic.factory.mail.terminal.TerminalCommand

class CommandFlow : FlowPerformBuilder<Executor<TerminalCommand>, TerminalCommand, String>() {

    private val dataHandlers = mutableMapOf<TerminalCommand, DataHandler<OperationResult>>()

    @Throws(BusyException::class)
    override fun width(subject: Executor<TerminalCommand>): CommandFlow {
        super.width(subject)
        return this
    }

    @Throws(BusyException::class)
    override fun perform(what: TerminalCommand): CommandFlow {
        super.perform(what)
        return this
    }

    @Throws(BusyException::class)
    fun perform(what: TerminalCommand, dataHandler: DataHandler<OperationResult>): CommandFlow {
        what.configuration[CommandConfiguration.OBTAIN_RESULT] = true
        super.perform(what)
        dataHandlers[what] = dataHandler
        return this
    }

    @Throws(BusyException::class)
    override fun onFinish(callback: FlowCallback<String>): CommandFlow {
        super.onFinish(callback)
        return this
    }

    @Throws(BusyException::class)
    override fun connect(flow: FlowBuilder<*, *, *>): CommandFlow {
        super.connect(flow)
        return this
    }

    @Throws(BusyException::class)
    override fun connect(provider: Obtain<FlowBuilder<*, *, *>>): CommandFlow {
        super.connect(provider)
        return this
    }

    @Throws(IllegalArgumentException::class)
    override fun getProcessingRecipe(
            subject: Executor<TerminalCommand>, operation: TerminalCommand
    ): ProcessingRecipe {

        return object : ProcessingRecipe {

            private var callback: FlowProcessingCallback? = null

            private val operationCallback = object : OperationResultListener {
                override fun onOperationPerformed(result: OperationResult) {

                    subject.unsubscribe(this)
                    val dataHandler = dataHandlers[operation]
                    dataHandler?.onData(result)
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
                try {
                    subject.execute(operation)
                } catch (e: Exception) {

                    subject.unsubscribe(operationCallback)
                    callback.onFinish(false, e.getMessage())
                }
            }
        }
    }
}