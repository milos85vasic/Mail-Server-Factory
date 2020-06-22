package net.milosvasic.factory.component.installer.step.condition

import net.milosvasic.factory.common.DataHandler
import net.milosvasic.factory.component.installer.step.RemoteOperationInstallationStep
import net.milosvasic.factory.execution.flow.callback.FlowCallback
import net.milosvasic.factory.execution.flow.implementation.CommandFlow
import net.milosvasic.factory.operation.OperationResult
import net.milosvasic.factory.remote.Connection
import net.milosvasic.factory.terminal.TerminalCommand

open class SkipCondition(protected val command: TerminalCommand) : RemoteOperationInstallationStep<Connection>() {

    protected var exception: Exception? = null

    private val dataHandler = object : DataHandler<OperationResult> {
        override fun onData(data: OperationResult?) {

            exception = data?.exception
        }
    }

    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    override fun getFlow(): CommandFlow {
        connection?.let { conn ->

            return CommandFlow()
                    .width(conn)
                    .perform(command, dataHandler)
        }
        throw IllegalArgumentException("No connection provided")
    }

    override fun getOperation() = SkipConditionOperation(exception == null)

    override fun getListener() = object : FlowCallback {
        override fun onFinish(success: Boolean) {
            finish(success)
        }
    }
}