package net.milosvasic.factory.component.installer.step

import net.milosvasic.factory.common.Notifying
import net.milosvasic.factory.common.Subscription
import net.milosvasic.factory.common.busy.Busy
import net.milosvasic.factory.common.busy.BusyWorker
import net.milosvasic.factory.execution.flow.callback.FlowCallback
import net.milosvasic.factory.execution.flow.implementation.CommandFlow
import net.milosvasic.factory.log
import net.milosvasic.factory.operation.Operation
import net.milosvasic.factory.operation.OperationResult
import net.milosvasic.factory.operation.OperationResultListener
import net.milosvasic.factory.remote.Connection
import net.milosvasic.factory.validation.Validator
import java.util.concurrent.ConcurrentLinkedQueue

abstract class RemoteOperationInstallationStep<T : Connection> :
        InstallationStep<T>(),
        Subscription<OperationResultListener>,
        Notifying<OperationResult> {

    private val busy = Busy()
    protected var connection: T? = null
    private val subscribers = ConcurrentLinkedQueue<OperationResultListener>()

    @Synchronized
    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    override fun execute(vararg params: T) {

        Validator.Arguments.validateSingle(params)
        BusyWorker.busy(busy)

        connection = params[0]
        if (connection == null) {
            throw IllegalArgumentException("Connection is null")
        }
        getFlow().onFinish(getListener()).run()
    }

    @Synchronized
    override fun notify(data: OperationResult) {
        val iterator = subscribers.iterator()
        while (iterator.hasNext()) {
            val listener = iterator.next()
            listener.onOperationPerformed(data)
        }
    }

    override fun subscribe(what: OperationResultListener) {
        subscribers.add(what)
    }

    override fun unsubscribe(what: OperationResultListener) {
        subscribers.remove(what)
    }

    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    abstract fun getFlow(): CommandFlow

    abstract fun getOperation(): Operation

    protected open fun finish(success: Boolean) {
        connection = null
        val operationResult = OperationResult(getOperation(), success)
        notify(operationResult)
    }

    protected open fun getListener() = object : FlowCallback {
        override fun onFinish(success: Boolean) {

            if (!success) {
                val what = this@RemoteOperationInstallationStep::class.simpleName
                log.e("Remote operation installation step failed: $what")
            }
            finish(success)
        }
    }
}