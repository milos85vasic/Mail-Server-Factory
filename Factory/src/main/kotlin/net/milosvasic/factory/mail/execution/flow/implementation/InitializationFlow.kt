package net.milosvasic.factory.mail.execution.flow.implementation

import net.milosvasic.factory.mail.EMPTY
import net.milosvasic.factory.mail.common.busy.BusyException
import net.milosvasic.factory.mail.common.initialization.Initializer
import net.milosvasic.factory.mail.execution.flow.FlowSimpleBuilder
import net.milosvasic.factory.mail.execution.flow.callback.FlowCallback
import net.milosvasic.factory.mail.execution.flow.processing.FlowProcessingCallback
import net.milosvasic.factory.mail.execution.flow.processing.ProcessingRecipe
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.operation.OperationResultListener
import net.milosvasic.factory.mail.terminal.TerminalCommand

class InitializationFlow : FlowSimpleBuilder<Initializer, String>() {

    @Throws(BusyException::class)
    override fun width(subject: Initializer): InitializationFlow {
        super.width(subject)
        return this
    }

    @Throws(BusyException::class)
    override fun onFinish(callback: FlowCallback<String>): InitializationFlow {
        super.onFinish(callback)
        return this
    }

    override fun getProcessingRecipe(subject: Initializer): ProcessingRecipe {

        return object : ProcessingRecipe {

            private var callback: FlowProcessingCallback? = null

            private val operationCallback = object : OperationResultListener {
                override fun onOperationPerformed(result: OperationResult) {
                    subject.unsubscribe(this)
                    val message = if (result.success) {
                        String.EMPTY
                    } else {
                        if (result.operation is TerminalCommand) {
                            "Initialization failed: ${result.operation.command}"
                        } else {
                            "Initialization failed: ${result.operation}"
                        }
                    }
                    callback?.onFinish(result.success, message)
                    callback = null
                }
            }

            override fun process(callback: FlowProcessingCallback) {
                this.callback = callback
                subject.subscribe(operationCallback)
                subject.initialize()
            }
        }
    }
}