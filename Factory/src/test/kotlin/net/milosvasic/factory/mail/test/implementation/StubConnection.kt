package net.milosvasic.factory.mail.test.implementation

import net.milosvasic.factory.mail.common.Notifying
import net.milosvasic.factory.mail.common.busy.BusyException
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.operation.OperationResultListener
import net.milosvasic.factory.mail.operation.command.CommandConfiguration
import net.milosvasic.factory.mail.os.OperatingSystem
import net.milosvasic.factory.mail.remote.Connection
import net.milosvasic.factory.mail.remote.Remote
import net.milosvasic.factory.mail.terminal.Terminal
import net.milosvasic.factory.mail.terminal.TerminalCommand
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