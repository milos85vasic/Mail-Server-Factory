package net.milosvasic.factory.execution.flow

import net.milosvasic.factory.common.CollectionWrapper
import net.milosvasic.factory.common.Wrapper
import net.milosvasic.factory.common.busy.Busy
import net.milosvasic.factory.common.busy.BusyDelegate
import net.milosvasic.factory.common.busy.BusyDelegation
import net.milosvasic.factory.common.busy.BusyException
import net.milosvasic.factory.execution.flow.callback.FlowCallback
import net.milosvasic.factory.execution.flow.processing.FlowProcessingCallback
import net.milosvasic.factory.log

abstract class FlowBuilder<T, D, C> : Flow<T, D>, BusyDelegation {

    abstract val subjects: CollectionWrapper<C>
    abstract val processingCallback: FlowProcessingCallback

    protected val busy = Busy()
    protected var currentSubject: Wrapper<T>? = null
    protected var subjectsIterator: Iterator<Wrapper<T>>? = null

    private var nextFlow: FlowBuilder<*, *, *>? = null
    private var callbacks: MutableSet<FlowCallback> = mutableSetOf()

    @Throws(BusyException::class)
    override fun width(subject: T): Flow<T, D> {
        if (busy.isBusy()) {
            throw BusyException()
        }
        currentSubject = Wrapper(subject)
        insertSubject()
        return this
    }

    @Throws(BusyException::class)
    override fun onFinish(callback: FlowCallback): Flow<T, D> {
        if (busy.isBusy()) {
            throw BusyException()
        }
        callbacks.add(callback)
        return this
    }

    @Throws(BusyException::class)
    open fun connect(flow: FlowBuilder<*, *, *>): FlowBuilder<T, D, C> {

        if (busy.isBusy()) {
            throw BusyException()
        }
        if (nextFlow == null) {
            nextFlow = flow
        } else {
            var flowToConnectTo = nextFlow
            while (flowToConnectTo?.nextFlow != null) {
                flowToConnectTo = flowToConnectTo.nextFlow
            }
            flowToConnectTo?.nextFlow = flow
        }
        return this
    }

    @Throws(BusyException::class)
    override fun run() {
        busy()
        currentSubject = null
        try {
            tryNext()
        } catch (e: IllegalArgumentException) {
            finish(e)
        } catch (e: IllegalStateException) {
            finish(e)
        }
    }

    fun isBusy(): Boolean {

        var busy = busy.isBusy()
        if (!busy) {
            nextFlow?.let {
                busy = it.isBusy()
            }
        }
        return busy
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

    protected fun finish(success: Boolean) {
        cleanupStates()

        fun finish(success: Boolean) {
            val iterator = callbacks.iterator()
            while (iterator.hasNext()) {
                val callback = iterator.next()
                callback.onFinish(success)
            }
            free()
        }

        if (success) {
            if (nextFlow == null) {
                finish(success)
            } else {

                try {
                    nextFlow?.let {
                        val callback = object : FlowCallback {
                            override fun onFinish(success: Boolean) {
                                finish(true)
                            }
                        }
                        it.onFinish(callback).run()
                    }
                } catch (e: Exception) {
                    log.e(e)
                }
            }
        } else {
            finish(success)
        }
    }

    protected fun finish(e: Exception) {

        log.e(e)
        finish(false)
    }

    protected open fun cleanupStates() {
        currentSubject = null
        subjectsIterator = null
    }

    protected abstract fun tryNext()

    protected abstract fun process()

    protected abstract fun insertSubject()
}


