package net.milosvasic.factory.component.database

import net.milosvasic.factory.common.busy.BusyException
import net.milosvasic.factory.operation.OperationResult
import net.milosvasic.factory.operation.OperationResultListener
import net.milosvasic.factory.remote.Connection
import net.milosvasic.factory.terminal.Terminal
import net.milosvasic.factory.terminal.TerminalCommand

class DatabaseConnection(
        val host: String,
        val port: Int,
        val user: String,
        val password: String,
        val entryPoint: Connection

) : Connection {

    @Synchronized
    @Throws(BusyException::class, IllegalArgumentException::class)
    override fun execute(what: TerminalCommand) {
        entryPoint.execute(what)
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

    override fun getRemote() = entryPoint.getRemote()

    override fun getRemoteOS() = entryPoint.getRemoteOS()

    override fun getTerminal(): Terminal = entryPoint.getTerminal()
}