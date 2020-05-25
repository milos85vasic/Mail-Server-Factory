package net.milosvasic.factory.mail.execution.flow.implementation

import net.milosvasic.factory.mail.common.Registration
import net.milosvasic.factory.mail.common.busy.BusyException
import net.milosvasic.factory.mail.common.obtain.Obtain
import net.milosvasic.factory.mail.execution.flow.Flow
import net.milosvasic.factory.mail.execution.flow.FlowBuilder
import net.milosvasic.factory.mail.execution.flow.FlowPerformBuilder
import net.milosvasic.factory.mail.execution.flow.callback.FlowCallback
import net.milosvasic.factory.mail.execution.flow.processing.FlowProcessingCallback
import net.milosvasic.factory.mail.execution.flow.processing.ProcessingRecipe
import net.milosvasic.factory.mail.getMessage

class RegistrationFlow<T> : FlowPerformBuilder<Registration<T>, T, String>() {

    @Throws(BusyException::class)
    override fun width(subject: Registration<T>): RegistrationFlow<T> {
        super.width(subject)
        return this
    }

    @Throws(BusyException::class)
    override fun perform(what: T): RegistrationFlow<T> {
        super.perform(what)
        return this
    }

    override fun perform(what: Obtain<T>): RegistrationFlow<T> {
        super.perform(what)
        return this
    }

    @Throws(BusyException::class)
    override fun onFinish(callback: FlowCallback<String>): RegistrationFlow<T> {
        super.onFinish(callback)
        return this
    }

    @Throws(BusyException::class)
    override fun connect(flow: FlowBuilder<*, *, *>): RegistrationFlow<T> {
        super.connect(flow)
        return this
    }

    @Throws(IllegalArgumentException::class)
    override fun getProcessingRecipe(subject: Registration<T>, operation: T): ProcessingRecipe {

        return object : ProcessingRecipe {

            private var callback: FlowProcessingCallback? = null

//            private val operationCallback = object : OperationResultListener {
//                override fun onOperationPerformed(result: OperationResult) {
//
//                    subject.unsubscribe(this)
//
//                    val message = if (result.success) {
//                        String.EMPTY
//                    } else {
//                        if (result.operation is TerminalCommand) {
//                            "Registration failed: ${result.operation.command}"
//                        } else {
//                            "Registration failed: ${result.operation}"
//                        }
//                    }
//                    callback?.onFinish(result.success, message)
//                    callback = null
//                }
//            }

            override fun process(callback: FlowProcessingCallback) {
                this.callback = callback
                // subject.subscribe(operationCallback)
                try {
                    subject.register(operation)
                } catch (e: Exception) {

                    // subject.unsubscribe(operationCallback)
                    callback.onFinish(false, e.getMessage())
                }
            }
        }
    }
}