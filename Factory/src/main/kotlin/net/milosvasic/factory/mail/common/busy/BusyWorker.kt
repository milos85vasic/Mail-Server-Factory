package net.milosvasic.factory.mail.common.busy

import net.milosvasic.factory.mail.common.Notifying
import net.milosvasic.factory.mail.common.Subscription
import net.milosvasic.factory.mail.log
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.operation.OperationResultListener
import net.milosvasic.factory.mail.remote.Connection
import java.util.concurrent.ConcurrentLinkedQueue

abstract class BusyWorker<T>(protected val entryPoint: Connection) :
        BusyDelegation,
        Subscription<OperationResultListener>,
        Notifying<OperationResult> {

    companion object : BusyDelegationParametrized<Busy> {

        @Synchronized
        @Throws(BusyException::class)
        override fun busy(what: Busy) {
            BusyDelegate.busy(what)
        }

        @Synchronized
        override fun free(what: Busy) {
            BusyDelegate.free(what)
        }
    }

    private val busy = Busy()
    private val subscribers = ConcurrentLinkedQueue<OperationResultListener>()

    override fun subscribe(what: OperationResultListener) {
        subscribers.add(what)
    }

    override fun unsubscribe(what: OperationResultListener) {
        subscribers.remove(what)
    }

    @Synchronized
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
        Companion.free(busy)
    }

    @Synchronized
    protected open fun free(success: Boolean) {
        free()
        notify(success)
    }

    protected fun onFailedResult(e: Exception) {
        log.e(e)
        free(false)
    }

    abstract fun onSuccessResult()

    abstract fun onFailedResult()

    abstract fun notify(success: Boolean)
}