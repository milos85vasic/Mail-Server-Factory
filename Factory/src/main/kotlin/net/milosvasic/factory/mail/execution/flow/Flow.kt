package net.milosvasic.factory.mail.execution.flow

import net.milosvasic.factory.mail.EMPTY
import net.milosvasic.factory.mail.common.busy.*

abstract class Flow<T, M> : Runnable, BusyDelegation {

    private val busy = Busy()
    private var currentSubject: T? = null
    private var currentOperation: M? = null
    private val subjects = mutableMapOf<T, List<M>>()
    private var currentOperations = mutableListOf<M>()
    private var subjectsIterator: Iterator<T>? = null
    private var operationsIterator: Iterator<M>? = null
    private var callback: FlowCallback = DefaultFlowCallback()

    private val processingCallback = object : FlowProcessingCallback {

        override fun onFinish(success: Boolean, message: String) {

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

    @Throws(BusyException::class)
    fun width(subject: T): Flow<T, M> {
        if (busy.isBusy()) {
            throw BusyException()
        }
        currentSubject?.let {
            subjects[it] = currentOperations
        }
        currentOperations = mutableListOf()
        currentSubject = subject
        return this
    }

    @Throws(BusyException::class)
    fun perform(what: M): Flow<T, M> {
        if (busy.isBusy()) {
            throw BusyException()
        }
        currentOperations.add(what)
        return this
    }

    @Throws(BusyException::class)
    fun onFinish(callback: FlowCallback): Flow<T, M> {
        if (busy.isBusy()) {
            throw BusyException()
        }
        this.callback = callback
        return this
    }

    @Synchronized
    @Throws(BusyException::class)
    override fun run() {
        busy()
        currentSubject?.let {
            subjects[it] = currentOperations
        }
        currentSubject = null
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

    abstract fun getProcessingRecipe(subject: T, operation: M): ProcessingRecipe

    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    private fun tryNext() {
        if (subjectsIterator == null) {
            subjectsIterator = subjects.keys.iterator()
        }
        if (currentSubject == null) {
            subjectsIterator?.let { sIterator ->
                if (sIterator.hasNext()) {
                    currentSubject = sIterator.next()
                } else {
                    finish(true)
                }
            }
        } else {
            if (operationsIterator == null) {
                subjects[currentSubject]?.let { operations ->
                    operationsIterator = operations.iterator()
                }
            }
        }
        if (operationsIterator == null) {
            throw IllegalArgumentException("No operations provided for subject: $currentSubject")
        }
        if (currentOperation == null) {
            operationsIterator?.let { oIterator ->
                if (oIterator.hasNext()) {
                    currentOperation = oIterator.next()
                } else {
                    currentSubject = null
                    tryNext()
                    return
                }
            }
        }
        process()
    }

    @Throws(IllegalStateException::class)
    private fun process() {
        if (currentSubject == null) {
            throw IllegalStateException("Current subject is null")
        }
        if (currentOperation == null) {
            throw IllegalStateException("Current operation is null")
        }
        currentSubject?.let { subject ->
            currentOperation?.let { operation ->
                val recipe = getProcessingRecipe(subject, operation)
                recipe.process(processingCallback)
            }
        }
    }

    private fun finish(success: Boolean, message: String = String.EMPTY) {
        callback.onFinish(success, message)
        currentOperation = null
        currentSubject = null
        free()
    }

    private fun finish(e: Exception){
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