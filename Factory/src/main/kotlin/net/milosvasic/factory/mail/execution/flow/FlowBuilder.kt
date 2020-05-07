package net.milosvasic.factory.mail.execution.flow

import net.milosvasic.factory.mail.EMPTY
import net.milosvasic.factory.mail.common.Wrapper
import net.milosvasic.factory.mail.common.busy.Busy
import net.milosvasic.factory.mail.common.busy.BusyDelegate
import net.milosvasic.factory.mail.common.busy.BusyDelegation
import net.milosvasic.factory.mail.common.busy.BusyException
import net.milosvasic.factory.mail.execution.flow.callback.DefaultFlowCallback
import net.milosvasic.factory.mail.execution.flow.callback.FlowCallback
import net.milosvasic.factory.mail.execution.flow.processing.FlowProcessingCallback
import net.milosvasic.factory.mail.execution.flow.processing.ProcessingRecipe

abstract class FlowBuilder<T, M, D> : Flow<T, M, D>, BusyDelegation {

    protected val busy = Busy()
    protected var currentOperation: M? = null
    protected var currentSubject: Wrapper<T>? = null
    protected var currentOperations = mutableListOf<M>()
    protected var operationsIterator: Iterator<M>? = null
    protected val subjects = mutableMapOf<Wrapper<T>, List<M>>()
    protected var subjectsIterator: Iterator<Wrapper<T>>? = null
    private var callback: FlowCallback<D> = DefaultFlowCallback()

    private val processingCallback = object : FlowProcessingCallback {
        override fun onFinish(success: Boolean, message: String, data: String?) {

            subjectsIterator?.let { sIterator ->
                operationsIterator?.let { oIterator ->
                    if (!sIterator.hasNext() && !oIterator.hasNext()) {
                        finish(true)
                    } else {
                        if (success) {
                            currentOperation = null
                            try {
                                tryNext()
                            } catch (e: IllegalArgumentException) {
                                finish(e)
                            } catch (e: IllegalStateException) {
                                finish(e)
                            }
                        } else {
                            finish(false, message)
                        }
                    }
                }
            }
        }
    }

    @Throws(BusyException::class)
    override fun width(subject: T): Flow<T, M, D> {
        if (busy.isBusy()) {
            throw BusyException()
        }
        currentSubject?.let {
            subjects[it] = currentOperations
        }
        currentOperations = mutableListOf()
        currentSubject = Wrapper(subject)
        return this
    }

    @Throws(BusyException::class)
    override fun onFinish(callback: FlowCallback<D>): Flow<T, M, D> {
        if (busy.isBusy()) {
            throw BusyException()
        }
        this.callback = callback
        return this
    }

    @Throws(BusyException::class)
    override fun run() {
        busy()
        currentSubject?.let {
            subjects[it] = currentOperations
        }
        currentSubject = null
        currentOperations = mutableListOf()
        try {
            tryNext()
        } catch (e: IllegalArgumentException) {
            finish(e)
        } catch (e: IllegalStateException) {
            finish(e)
        }
    }

    @Synchronized
    @Throws(BusyException::class)
    override fun busy() {
        BusyDelegate.busy(busy)
    }

    @Synchronized
    override fun free() {
        BusyDelegate.free(busy)
    }

    @Throws(IllegalStateException::class)
    protected fun process() {
        if (currentSubject == null) {
            throw IllegalStateException("Current subject is null")
        }
        if (currentOperation == null) {
            throw IllegalStateException("Current operation is null")
        }
        currentSubject?.let { subject ->
            currentOperation?.let { operation ->
                val recipe = getProcessingRecipe(subject.content, operation)
                recipe.process(processingCallback)
            }
        }
    }

    protected fun finish(success: Boolean, message: String = String.EMPTY) {
        currentSubject = null
        currentOperation = null
        subjectsIterator = null
        operationsIterator = null
        currentOperations = mutableListOf()
        callback.onFinish(success, message)
        free()
    }

    protected abstract fun tryNext()

    protected abstract fun getProcessingRecipe(subject: T, operation: M): ProcessingRecipe

    private fun finish(e: Exception) {
        var message = String.EMPTY
        e.message?.let {
            message = it
        }
        if (message == String.EMPTY) {
            e::class.simpleName?.let {
                message = it
            }
        }
        finish(false, message)
    }
}