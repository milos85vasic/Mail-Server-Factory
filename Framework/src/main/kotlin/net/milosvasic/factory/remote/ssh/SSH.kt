package net.milosvasic.factory.remote.ssh

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
import net.milosvasic.factory.terminal.WrappedTerminalCommand
import java.util.concurrent.ConcurrentLinkedQueue

open class SSH(private val remote: Remote) :
        Connection,
        Notifying<OperationResult> {

    private val terminal = Terminal()

    private var operatingSystem = OperatingSystem()
    private val subscribers = ConcurrentLinkedQueue<OperationResultListener>()

    private val listener = object : OperationResultListener {
        override fun onOperationPerformed(result: OperationResult) {
            if (result.operation is SSHCommand) {

                val remoteCommand = result.operation.remoteCommand
                val sshResult = if (remoteCommand is WrappedTerminalCommand) {

                    OperationResult(
                            remoteCommand.wrappedCommand,
                            result.success,
                            result.data,
                            result.exception
                    )
                } else {

                    OperationResult(
                            remoteCommand,
                            result.success,
                            result.data,
                            result.exception
                    )
                }
                notify(sshResult)
            }
        }
    }

    init {
        terminal.subscribe(listener)
    }

    override fun getTerminal() = terminal

    @Synchronized
    @Throws(BusyException::class, IllegalArgumentException::class)
    override fun execute(what: TerminalCommand) {
        val command = SSHCommand(remote, what, what.configuration)
        terminal.execute(command)
    }

    @Synchronized
    @Throws(BusyException::class, IllegalArgumentException::class)
    open fun execute(data: TerminalCommand, obtainOutput: Boolean) {
        val command = SSHCommand(remote, data)
        command.configuration[CommandConfiguration.OBTAIN_RESULT] = obtainOutput
        terminal.execute(command)
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
        return remote
    }

    override fun getRemoteOS(): OperatingSystem {
        return operatingSystem
    }
}