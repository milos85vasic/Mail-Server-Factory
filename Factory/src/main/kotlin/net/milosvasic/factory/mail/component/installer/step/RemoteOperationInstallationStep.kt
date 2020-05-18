package net.milosvasic.factory.mail.component.installer.step

import net.milosvasic.factory.mail.common.Notifying
import net.milosvasic.factory.mail.common.Subscription
import net.milosvasic.factory.mail.common.busy.Busy
import net.milosvasic.factory.mail.common.busy.BusyWorker
import net.milosvasic.factory.mail.execution.flow.callback.FlowCallback
import net.milosvasic.factory.mail.execution.flow.implementation.CommandFlow
import net.milosvasic.factory.mail.log
import net.milosvasic.factory.mail.operation.Operation
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.operation.OperationResultListener
import net.milosvasic.factory.mail.remote.Connection
import net.milosvasic.factory.mail.validation.Validator
import java.util.concurrent.ConcurrentLinkedQueue

abstract class RemoteOperationInstallationStep<T : Connection> :
        InstallationStep<T>(),
        Subscription<OperationResultListener>,
        Notifying<OperationResult> {

    private val busy = Busy()
    protected var connection: T? = null
    private val subscribers = ConcurrentLinkedQueue<OperationResultListener>()

    private val listener = object : FlowCallback<String> {
        override fun onFinish(
                success: Boolean,
                message: String,
                data: String?
        ) {

            if (!success) {
                log.e(message)
            }
            finish(success)
        }
    }

    @Synchronized
    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    override fun execute(vararg params: T) {

        Validator.Arguments.validateSingle(params)
        BusyWorker.busy(busy)

        connection = params[0]
        if (connection == null) {
            throw IllegalArgumentException("Connection is null")
        }
        getFlow().onFinish(listener).run()
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
}