package net.milosvasic.factory.execution.flow.implementation

import net.milosvasic.factory.common.DataHandler
import net.milosvasic.factory.common.busy.BusyException
import net.milosvasic.factory.common.execution.Executor
import net.milosvasic.factory.execution.flow.FlowBuilder
import net.milosvasic.factory.execution.flow.FlowPerformBuilder
import net.milosvasic.factory.execution.flow.callback.FlowCallback
import net.milosvasic.factory.execution.flow.processing.FlowProcessingCallback
import net.milosvasic.factory.execution.flow.processing.ProcessingRecipe
import net.milosvasic.factory.log
import net.milosvasic.factory.operation.OperationResult
import net.milosvasic.factory.operation.OperationResultListener
import net.milosvasic.factory.operation.command.CommandConfiguration
import net.milosvasic.factory.terminal.TerminalCommand

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
    override fun onFinish(callback: FlowCallback): CommandFlow {
        super.onFinish(callback)
        return this
    }

    @Throws(BusyException::class)
    override fun connect(flow: FlowBuilder<*, *, *>): CommandFlow {
        super.connect(flow)
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
                    callback?.onFinish(result.success)
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
                    log.e(e)
                    callback.onFinish(false)
                    this.callback = null
                }
            }
        }
    }
}