package net.milosvasic.factory.mail.common.busy

import net.milosvasic.factory.mail.EMPTY
import net.milosvasic.factory.mail.common.Notifying
import net.milosvasic.factory.mail.common.Subscription
import net.milosvasic.factory.mail.component.Component
import net.milosvasic.factory.mail.component.Termination
import net.milosvasic.factory.mail.log
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.operation.OperationResultListener
import net.milosvasic.factory.mail.remote.Connection

abstract class BusyWorker<T>(protected val entryPoint: Connection) :
    Component(),
    BusyDelegation,
    Subscription<OperationResultListener>,
    Notifying<OperationResult>,
    Termination {

    companion object : BusyDelegationParametrized<Busy> {

        @Synchronized
        @Throws(BusyException::class)
        override fun busy(what: Busy) {
            if (what.isBusy()) {
                throw BusyException()
            }
            what.setBusy(true)
        }

        @Synchronized
        override fun free(what: Busy) {
            what.setBusy(false)
        }
    }

    protected var command = String.EMPTY
    protected var iterator: Iterator<T>? = null

    private val busy = Busy()
    private val subscribers = mutableSetOf<OperationResultListener>()

    private val listener = object : OperationResultListener {
        override fun onOperationPerformed(result: OperationResult) {

            handleResult(result)
        }
    }

    init {
        entryPoint.subscribe(listener)
    }

    override fun subscribe(what: OperationResultListener) {
        subscribers.add(what)
    }

    override fun unsubscribe(what: OperationResultListener) {
        subscribers.remove(what)
    }

    protected fun attach(subscription: Subscription<OperationResultListener>?) {
        subscription?.subscribe(listener)
    }

    protected fun detach(subscription: Subscription<OperationResultListener>?) {
        subscription?.unsubscribe(listener)
    }

    override fun notify(data: OperationResult) {
        val iterator = subscribers.iterator()
        while (iterator.hasNext()) {
            val listener = iterator.next()
            listener.onOperationPerformed(data)
        }
    }

    @Synchronized
    @Throws(BusyException::class)
    override fun busy() {
        Companion.busy(busy)
    }

    @Synchronized
    override fun free() {
        iterator = null
        Companion.free(busy)
    }

    @Synchronized
    protected open fun free(success: Boolean) {
        free()
        command = String.EMPTY
        notify(success)
    }

    protected fun onFailedResult(e: IllegalStateException) {
        log.e(e)
        free(false)
    }

    override fun terminate() {
        log.v("Shutting down: $this")
        entryPoint.unsubscribe(listener)
    }

    abstract fun tryNext()

    abstract fun onSuccessResult()

    abstract fun onFailedResult()

    abstract fun handleResult(result: OperationResult)

    abstract fun notify(success: Boolean)
}