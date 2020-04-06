package net.milosvasic.factory.mail.component.installer.step.condition

import net.milosvasic.factory.mail.common.Notifying
import net.milosvasic.factory.mail.common.Subscription
import net.milosvasic.factory.mail.common.busy.Busy
import net.milosvasic.factory.mail.common.busy.BusyWorker
import net.milosvasic.factory.mail.component.installer.step.InstallationStep
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.operation.OperationResultListener
import net.milosvasic.factory.mail.remote.Connection
import net.milosvasic.factory.mail.remote.ssh.SSH
import net.milosvasic.factory.mail.remote.ssh.SSHCommand
import java.util.concurrent.ConcurrentLinkedQueue

class Condition(private val command: String) :
    InstallationStep<Connection>(),
    Subscription<OperationResultListener>,
    Notifying<OperationResult> {

    private val busy = Busy()
    private var connection: Connection? = null
    private val subscribers = ConcurrentLinkedQueue<OperationResultListener>()

    private val listener = object : OperationResultListener {
        override fun onOperationPerformed(result: OperationResult) {
            when (result.operation) {
                is SSHCommand -> {
                    if (result.operation.command.endsWith(command)) {

                        finish(result.success)
                    }
                }
            }
        }
    }

    @Synchronized
    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    override fun execute(vararg params: Connection) {

        if (params.size > 1 || params.isEmpty()) {
            throw IllegalArgumentException("Expected 1 argument")
        }
        BusyWorker.busy(busy)

        connection = params[0]
        if (connection == null) {
            throw IllegalArgumentException("Connection is null.")
        }
        connection?.subscribe(listener)
        connection?.execute(command)
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

    private fun finish(success: Boolean) {
        connection?.unsubscribe(listener)
        connection = null

        // TODO: Obtain success information for the operation!
        val operation = ConditionOperation(false)
        val operationResult = OperationResult(operation, success)
        notify(operationResult)
    }
}