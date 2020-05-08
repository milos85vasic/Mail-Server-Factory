package net.milosvasic.factory.mail.execution.flow

import net.milosvasic.factory.mail.common.CollectionWrapper
import net.milosvasic.factory.mail.common.Wrapper
import net.milosvasic.factory.mail.common.busy.BusyException
import net.milosvasic.factory.mail.execution.flow.processing.FlowProcessingCallback
import net.milosvasic.factory.mail.execution.flow.processing.ProcessingRecipe

abstract class FlowPerformBuilder<T, M, D> : FlowBuilder<T, D, MutableMap<Wrapper<T>, MutableList<M>>>(), FlowPerform<T, M, D> {

    private var currentOperation: M? = null
    private var operationsIterator: Iterator<M>? = null
    private val collectionWrapper = CollectionWrapper<MutableMap<Wrapper<T>, MutableList<M>>>(mutableMapOf())

    override val subjects: CollectionWrapper<MutableMap<Wrapper<T>, MutableList<M>>>
        get() = collectionWrapper

    override val processingCallback: FlowProcessingCallback
        get() = object : FlowProcessingCallback {
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

    override fun insertSubject() {
        currentSubject?.let {
            subjects.get()[it] = mutableListOf()
        }
    }

    @Throws(BusyException::class)
    override fun perform(what: M): Flow<T, D> {
        if (busy.isBusy()) {
            throw BusyException()
        }
        subjects.get()[currentSubject]?.add(what)
        return this
    }

    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    override fun tryNext() {
        if (subjects.get().isEmpty()) {
            throw IllegalArgumentException("No subjects provided")
        }
        subjects.get().keys.forEach {
            if (subjects.get()[it] == null) {
                throw IllegalArgumentException("Null operations provided for subject: ${it.content}")
            }
            subjects.get()[it]?.let { children ->
                if (children.isEmpty()) {
                    throw IllegalArgumentException("No operations provided for subject: ${it.content}")
                }
            }
        }
        if (subjectsIterator == null) {
            subjectsIterator = subjects.get().keys.iterator()
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
                    subjects.get()[it]?.let { operations ->
                        operationsIterator = operations.iterator()
                    }
                }
            }
        }
        if (operationsIterator == null) {
            subjects.get()[currentSubject]?.let {
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

    override fun cleanupStates() {
        super.cleanupStates()
        currentOperation = null
        operationsIterator = null
    }

    @Throws(IllegalStateException::class)
    override fun process() {
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

    protected abstract fun getProcessingRecipe(subject: T, operation: M): ProcessingRecipe
}