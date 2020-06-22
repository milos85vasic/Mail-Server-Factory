package net.milosvasic.factory.test.implementation

import net.milosvasic.factory.common.Notifying
import net.milosvasic.factory.common.busy.BusyException
import net.milosvasic.factory.operation.OperationResult
import net.milosvasic.factory.operation.OperationResultListener
import net.milosvasic.factory.operation.command.CommandConfiguration
import net.milosvasic.factory.os.OperatingSystem
import net.milosvasic.factory.remote.Connection
import net.milosvasic.factory.remote.Remote
import net.milosvasic.factory.terminal.Terminal
import net.milosvasic.factory.terminal.TerminalCommand
import java.util.concurrent.ConcurrentLinkedQueue

class StubConnection : Connection, Notifying<OperationResult> {

    private val term = Terminal()

    private val subscribers = ConcurrentLinkedQueue<OperationResultListener>()

    private val listener = object : OperationResultListener {
        override fun onOperationPerformed(result: OperationResult) {
            notify(result)
        }
    }

    override fun getTerminal() = term

    init {
        term.subscribe(listener)
    }

    @Synchronized
    @Throws(BusyException::class, IllegalArgumentException::class)
    override fun execute(what: TerminalCommand) {
        term.execute(what)
    }

    @Synchronized
    @Throws(BusyException::class, IllegalArgumentException::class)
    fun execute(data: TerminalCommand, obtainOutput: Boolean) {
        data.configuration[CommandConfiguration.OBTAIN_RESULT] = obtainOutput
        term.execute(data)
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

    override fun getRemote(): Remote {
        TODO("Not yet implemented")
    }

    override fun getRemoteOS(): OperatingSystem {
        TODO("Not yet implemented")
    }
}