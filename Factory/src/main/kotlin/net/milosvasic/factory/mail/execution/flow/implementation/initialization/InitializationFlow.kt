package net.milosvasic.factory.mail.execution.flow.implementation.initialization

import net.milosvasic.factory.mail.EMPTY
import net.milosvasic.factory.mail.common.busy.BusyException
import net.milosvasic.factory.mail.common.initialization.InitializationOperation
import net.milosvasic.factory.mail.common.initialization.Initializer
import net.milosvasic.factory.mail.common.initialization.TerminationOperation
import net.milosvasic.factory.mail.common.obtain.Obtain
import net.milosvasic.factory.mail.execution.flow.FlowBuilder
import net.milosvasic.factory.mail.execution.flow.FlowSimpleBuilder
import net.milosvasic.factory.mail.execution.flow.callback.FlowCallback
import net.milosvasic.factory.mail.execution.flow.processing.FlowProcessingCallback
import net.milosvasic.factory.mail.execution.flow.processing.ProcessingRecipe
import net.milosvasic.factory.mail.getMessage
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.operation.OperationResultListener

class InitializationFlow : FlowSimpleBuilder<Initializer, String>() {

    private val initializationHandlers = mutableMapOf<Initializer, InitializationHandler>()

    @Throws(BusyException::class)
    override fun width(subject: Initializer): InitializationFlow {
        super.width(subject)
        return this
    }

    @Throws(BusyException::class)
    fun width(subject: Initializer, handler: InitializationHandler): InitializationFlow {
        super.width(subject)
        initializationHandlers[subject] = handler
        return this
    }

    @Throws(BusyException::class)
    fun handler(handler: InitializationHandler): InitializationFlow {
        val subject = subjects.get().last()
        initializationHandlers[subject.content] = handler
        return this
    }

    @Throws(BusyException::class)
    override fun onFinish(callback: FlowCallback<String>): InitializationFlow {
        super.onFinish(callback)
        return this
    }

    @Throws(BusyException::class)
    override fun connect(flow: FlowBuilder<*, *, *>): InitializationFlow {
        super.connect(flow)
        return this
    }

    @Throws(BusyException::class)
    override fun connect(provider: Obtain<FlowBuilder<*, *, *>>): InitializationFlow {
        super.connect(provider)
        return this
    }

    @Throws(IllegalArgumentException::class)
    override fun getProcessingRecipe(subject: Initializer): ProcessingRecipe {

        return object : ProcessingRecipe {

            private var callback: FlowProcessingCallback? = null

            private val operationCallback = object : OperationResultListener {
                override fun onOperationPerformed(result: OperationResult) {

                    val handler = initializationHandlers[subject]
                    when (result.operation) {
                        is InitializationOperation -> {
                            val message = if (result.success) {
                                String.EMPTY
                            } else {
                                "Initialization failed for $subject"
                            }
                            if (handler == null) {
                                subject.unsubscribe(this)
                            } else {
                                handler.onInitialization(subject, result.success)
                            }
                            callback?.onFinish(result.success, message)
                            callback = null
                        }
                        is TerminationOperation -> {
                            subject.unsubscribe(this)
                            handler?.let {
                                handler.onTermination(subject, result.success)
                            }
                        }
                    }
                }
            }

            override fun process(callback: FlowProcessingCallback) {
                this.callback = callback
                subject.subscribe(operationCallback)
                try {
                    subject.initialize()
                } catch (e: Exception) {

                    subject.unsubscribe(operationCallback)
                    callback.onFinish(false, e.getMessage())
                }
            }
        }
    }
}