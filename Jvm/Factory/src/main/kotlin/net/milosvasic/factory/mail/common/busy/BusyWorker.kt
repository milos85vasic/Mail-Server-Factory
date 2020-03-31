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
    Subscription<OperationResultListener>,
    Notifying<OperationResult>,
    Termination {

    protected var command = String.EMPTY
    protected var iterator: Iterator<T>? = null

    private val busy = Busy()
    private val subscribers = mutableSetOf<OperationResultListener>()

    protected val listener = object : OperationResultListener {
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

    @Throws(BusyException::class)
    protected fun busy() {
        if (busy.isBusy()) {
            throw BusyException()
        }
        busy.setBusy(true)
    }

    protected open fun unBusy(success: Boolean) {
        notify(success)
        command = String.EMPTY
        unBusy()
    }

    override fun terminate() {
        log.v("Shutting down: $this")
        entryPoint.unsubscribe(listener)
    }

    private fun unBusy() {
        iterator = null
        busy.setBusy(false)
    }

    abstract fun tryNext()

    abstract fun onSuccessResult()

    abstract fun onFailedResult()

    abstract fun handleResult(result: OperationResult)

    abstract fun notify(success: Boolean)
}