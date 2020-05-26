package net.milosvasic.factory.mail.execution.flow

import net.milosvasic.factory.mail.EMPTY
import net.milosvasic.factory.mail.common.CollectionWrapper
import net.milosvasic.factory.mail.common.Wrapper
import net.milosvasic.factory.mail.common.busy.Busy
import net.milosvasic.factory.mail.common.busy.BusyDelegate
import net.milosvasic.factory.mail.common.busy.BusyDelegation
import net.milosvasic.factory.mail.common.busy.BusyException
import net.milosvasic.factory.mail.common.obtain.Obtain
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

    protected var nextFlow: Obtain<FlowBuilder<*, *, *>?>? = null
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

        if (busy.isBusy()) {
            throw BusyException()
        }
        if (nextFlow?.obtain() == null) {
            nextFlow = object : Obtain<FlowBuilder<*, *, *>> {
                override fun obtain(): FlowBuilder<*, *, *> {
                    return flow
                }
            }
        } else {
            val flowToConnectTo = getFlowToConnectTo()
            flowToConnectTo.obtain()?.nextFlow = object : Obtain<FlowBuilder<*, *, *>?>{
                override fun obtain(): FlowBuilder<*, *, *>? {
                    return flow
                }
            }
        }
        return this
    }

    @Throws(BusyException::class)
    open fun connect(provider: Obtain<FlowBuilder<*, *, *>>): FlowBuilder<T, D, C> {

        if (busy.isBusy()) {
            throw BusyException()
        }
        if (nextFlow?.obtain() == null) {
            nextFlow = provider
        } else {
            val flowToConnectTo = getFlowToConnectTo()
            flowToConnectTo.obtain()?.nextFlow = provider
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
            nextFlow?.obtain()?.let {
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
                nextFlow?.obtain()?.run()
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

    private fun getFlowToConnectTo(): Obtain<FlowBuilder<*, *, *>?> {
        var flowToConnectTo: Obtain<FlowBuilder<*, *, *>?> = object : Obtain<FlowBuilder<*, *, *>?> {
            override fun obtain(): FlowBuilder<*, *, *>? {
                return nextFlow?.obtain()
            }
        }
        while (flowToConnectTo.obtain()?.nextFlow?.obtain() != null) {
            flowToConnectTo.obtain()?.nextFlow?.let {
                flowToConnectTo = it
            }
        }
        return flowToConnectTo
    }
}


