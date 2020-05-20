package net.milosvasic.factory.mail.execution.flow

import net.milosvasic.factory.mail.EMPTY
import net.milosvasic.factory.mail.common.CollectionWrapper
import net.milosvasic.factory.mail.common.Wrapper
import net.milosvasic.factory.mail.common.busy.Busy
import net.milosvasic.factory.mail.common.busy.BusyDelegate
import net.milosvasic.factory.mail.common.busy.BusyDelegation
import net.milosvasic.factory.mail.common.busy.BusyException
import net.milosvasic.factory.mail.execution.flow.callback.DefaultApplicationCallback
import net.milosvasic.factory.mail.execution.flow.callback.FlowCallback
import net.milosvasic.factory.mail.execution.flow.processing.FlowProcessingCallback
import net.milosvasic.factory.mail.log

abstract class FlowBuilder<T, D, C> : Flow<T, D>, BusyDelegation {

    abstract val subjects: CollectionWrapper<C>
    abstract val processingCallback: FlowProcessingCallback

    protected val busy = Busy()
    protected var currentSubject: Wrapper<T>? = null
    protected var subjectsIterator: Iterator<Wrapper<T>>? = null

    private var nextFlow: FlowBuilder<*, *, *>? = null
    private var callback: FlowCallback<D> = DefaultApplicationCallback()

    @Throws(BusyException::class)
    override fun width(subject: T): Flow<T, D> {
        if (busy.isBusy()) {
            throw BusyException()
        }
        currentSubject = Wrapper(subject)
        insertSubject()
        return this
    }

    @Throws(BusyException::class, IllegalStateException::class)
    override fun onFinish(callback: FlowCallback<D>): Flow<T, D> {
        if (busy.isBusy()) {
            throw BusyException()
        }
        this.callback = callback
        return this
    }

    @Throws(BusyException::class)
    open fun connect(flow: FlowBuilder<*, *, *>): FlowBuilder<T, D, C> {

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

    protected fun finish(success: Boolean, message: String = String.EMPTY) {
        cleanupStates()
        if (success) {
            try {
                nextFlow?.run()
            } catch (e: Exception) {
                log.e(e)
            }
        }
        callback.onFinish(success, message)
        free()
    }

    protected fun finish(e: Exception) {
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

    protected open fun cleanupStates() {
        currentSubject = null
        subjectsIterator = null
    }

    protected abstract fun tryNext()

    protected abstract fun process()

    protected abstract fun insertSubject()
}


