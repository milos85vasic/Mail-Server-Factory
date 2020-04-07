package net.milosvasic.factory.mail.component.installer.step

import net.milosvasic.factory.mail.common.Notifying
import net.milosvasic.factory.mail.common.Subscription
import net.milosvasic.factory.mail.common.busy.Busy
import net.milosvasic.factory.mail.common.busy.BusyWorker
import net.milosvasic.factory.mail.operation.Operation
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.operation.OperationResultListener
import net.milosvasic.factory.mail.remote.Connection
import java.util.concurrent.ConcurrentLinkedQueue

abstract class RemoteOperationInstallationStep<T : Connection> :
        InstallationStep<T>(), Subscription<OperationResultListener>, Notifying<OperationResult> {

    private val busy = Busy()
    protected var connection: T? = null
    private val subscribers = ConcurrentLinkedQueue<OperationResultListener>()

    private val listener = object : OperationResultListener {
        override fun onOperationPerformed(result: OperationResult) {
            handleResult(result)
        }
    }

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
    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    override fun execute(vararg params: T) {

        if (params.size > 1 || params.isEmpty()) {
            throw IllegalArgumentException("Expected 1 argument")
        }
        BusyWorker.busy(busy)

        connection = params[0]
        if (connection == null) {
            throw IllegalArgumentException("Connection is null.")
        }
        connection?.subscribe(listener)
    }

    protected fun finish(success: Boolean, operation: Operation) {
        connection?.unsubscribe(listener)
        connection = null
        val operationResult = OperationResult(operation, success)
        notify(operationResult)
    }

    abstract fun handleResult(result: OperationResult)
}