package net.milosvasic.factory.mail.execution.flow

import net.milosvasic.factory.mail.EMPTY
import net.milosvasic.factory.mail.common.Wrapper
import net.milosvasic.factory.mail.common.busy.Busy
import net.milosvasic.factory.mail.common.busy.BusyDelegate
import net.milosvasic.factory.mail.common.busy.BusyDelegation
import net.milosvasic.factory.mail.common.busy.BusyException

abstract class FlowBuilder<T, M, D> : Flow<T, M, D>, BusyDelegation {

    private val busy = Busy()
    private var currentOperation: M? = null
    private var currentSubject: Wrapper<T>? = null
    private var currentOperations = mutableListOf<M>()
    private val subjects = mutableMapOf<Wrapper<T>, List<M>>()
    private var subjectsIterator: Iterator<Wrapper<T>>? = null
    private var operationsIterator: Iterator<M>? = null
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
    override fun perform(what: M): Flow<T, M, D> {
        if (busy.isBusy()) {
            throw BusyException()
        }
        currentOperations.add(what)
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

    protected abstract fun getProcessingRecipe(subject: T, operation: M): ProcessingRecipe

    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    private fun tryNext() {
        if (subjects.isEmpty()) {
            throw IllegalArgumentException("No subjects provided")
        }
        subjects.keys.forEach {
            if (subjects[it] == null) {
                throw IllegalArgumentException("Null operations provided for subject: $it")
            }
            subjects[it]?.let { children ->
                if (children.isEmpty()) {
                    throw IllegalArgumentException("No operations provided for subject: $it")
                }
            }
        }
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
                currentSubject?.let {
                    subjects[it]?.let { operations ->
                        operationsIterator = operations.iterator()
                    }
                }
            }
        }
        if (operationsIterator == null) {
            subjects[currentSubject]?.let {
                operationsIterator = it.iterator()
            }
        }
        if (currentOperation == null) {
            operationsIterator?.let { oIterator ->
                if (oIterator.hasNext()) {
                    currentOperation = oIterator.next()
                } else {
                    currentSubject = null
                    operationsIterator = null
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
                val recipe = getProcessingRecipe(subject.content, operation)
                recipe.process(processingCallback)
            }
        }
    }

    private fun finish(success: Boolean, message: String = String.EMPTY) {
        currentSubject = null
        currentOperation = null
        subjectsIterator = null
        operationsIterator = null
        currentOperations = mutableListOf()
        callback.onFinish(success, message)
        free()
    }

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