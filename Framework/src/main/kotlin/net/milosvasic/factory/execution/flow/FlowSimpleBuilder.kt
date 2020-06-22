package net.milosvasic.factory.execution.flow

import net.milosvasic.factory.common.CollectionWrapper
import net.milosvasic.factory.common.Wrapper
import net.milosvasic.factory.execution.flow.processing.FlowProcessingCallback
import net.milosvasic.factory.execution.flow.processing.FlowProcessingData
import net.milosvasic.factory.execution.flow.processing.ProcessingRecipe

abstract class FlowSimpleBuilder<T, D> : FlowBuilder<T, D, MutableList<Wrapper<T>>>() {

    private val collectionWrapper = CollectionWrapper<MutableList<Wrapper<T>>>(mutableListOf())

    override val subjects: CollectionWrapper<MutableList<Wrapper<T>>>
        get() = collectionWrapper

    override val processingCallback: FlowProcessingCallback
        get() = object : FlowProcessingCallback {
            override fun onFinish(success: Boolean, data: FlowProcessingData?) {
                tryNextSubject(success, data)
            }
        }

    override fun insertSubject() {
        currentSubject?.let {
            subjects.get().add(it)
        }
    }

    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    override fun tryNext() {
        if (subjects.get().isEmpty()) {
            throw IllegalArgumentException("No subjects provided")
        }
        if (subjectsIterator == null) {
            subjectsIterator = subjects.get().iterator()
        }
        subjectsIterator?.let { sIterator ->
            if (sIterator.hasNext()) {
                currentSubject = sIterator.next()
            } else {
                finish(true)
                return
            }
        }
        process()
    }

    @Throws(IllegalStateException::class, IllegalArgumentException::class)
    override fun process() {
        if (currentSubject == null) {
            throw IllegalStateException("Current subject is null")
        }
        currentSubject?.let { subject ->
            val recipe = getProcessingRecipe(subject.content)
            recipe.process(processingCallback)
        }
    }

    @Throws(IllegalArgumentException::class)
    protected abstract fun getProcessingRecipe(subject: T): ProcessingRecipe

    protected open fun tryNextSubject(
            success: Boolean,
            data: FlowProcessingData?
    ) {
        subjectsIterator?.let { sIterator ->
            if (!sIterator.hasNext()) {
                finish(success)
            } else {
                if (success) {
                    try {
                        tryNext()
                    } catch (e: IllegalArgumentException) {
                        finish(e)
                    } catch (e: IllegalStateException) {
                        finish(e)
                    }
                } else {
                    finish(false)
                }
            }
        }
    }
}