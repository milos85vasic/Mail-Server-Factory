package net.milosvasic.factory.mail.component.database

import net.milosvasic.factory.mail.component.database.command.DatabaseCommand
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.operation.OperationResultListener
import net.milosvasic.factory.mail.remote.Connection
import net.milosvasic.factory.mail.terminal.Terminal
import net.milosvasic.factory.mail.terminal.TerminalCommand

class DatabaseConnection(
        val host: String,
        val port: Int,
        val user: String,
        val password: String,
        val entryPoint: Connection

) : Connection {

    override fun getRemote() = entryPoint.getRemote()

    override fun getRemoteOS() = entryPoint.getRemoteOS()

    override fun getTerminal(): Terminal = entryPoint.getTerminal()

    @Throws(IllegalArgumentException::class)
    override fun execute(what: TerminalCommand) {

        if (what is DatabaseCommand) {

            what.execute(this)
        } else {

            val name = DatabaseCommand::class.simpleName
            val thisName = this::class.simpleName
            throw IllegalArgumentException("Only $name is supported by $thisName")
        }
    }

    override fun subscribe(what: OperationResultListener) {
        entryPoint.subscribe(what)
    }

    override fun unsubscribe(what: OperationResultListener) {
        entryPoint.unsubscribe(what)
    }

    override fun notify(data: OperationResult) {
        entryPoint.notify(data)
    }
}