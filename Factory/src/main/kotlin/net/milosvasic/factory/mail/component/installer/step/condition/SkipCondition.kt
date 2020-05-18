package net.milosvasic.factory.mail.component.installer.step.condition

import net.milosvasic.factory.mail.common.DataHandler
import net.milosvasic.factory.mail.component.installer.step.RemoteOperationInstallationStep
import net.milosvasic.factory.mail.execution.flow.implementation.CommandFlow
import net.milosvasic.factory.mail.remote.Connection

open class SkipCondition(protected val command: String) : RemoteOperationInstallationStep<Connection>() {

    var result = false

    /* FIXME: Replaced with data handler:
    override fun handleResult(result: OperationResult) {
        when (result.operation) {
            is TerminalCommand -> {
                if (result.operation.command.endsWith(command)) {
                    finish(result.success, result.exception == null)
                }
            }
        }
    }
     */

    private val dataHandler = object : DataHandler<String> {
        override fun onData(data: String?) {

            data?.let {
                result = true
            }
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

    override fun getOperation() = SkipConditionOperation(result)
}