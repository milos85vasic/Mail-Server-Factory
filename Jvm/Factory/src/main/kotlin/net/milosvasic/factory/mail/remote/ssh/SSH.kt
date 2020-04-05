package net.milosvasic.factory.mail.remote.ssh

import net.milosvasic.factory.mail.common.Notifying
import net.milosvasic.factory.mail.remote.Connection
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.operation.OperationResultListener
import net.milosvasic.factory.mail.os.OperatingSystem
import net.milosvasic.factory.mail.remote.Remote
import net.milosvasic.factory.mail.terminal.Terminal
import java.util.concurrent.ConcurrentLinkedQueue

class SSH(private val remote: Remote) :
    Connection,
    Notifying<OperationResult> {

    val terminal = Terminal()

    private var operatingSystem = OperatingSystem()
    private val subscribers = ConcurrentLinkedQueue<OperationResultListener>()

    private val listener = object : OperationResultListener {
        override fun onOperationPerformed(result: OperationResult) {
            notify(result)
        }
    }

    init {
        terminal.subscribe(listener)
    }

    override fun execute(what: String) {
        terminal.execute(SSHCommand(remote, what))
    }

    fun execute(data: String, obtainCommandOutput: Boolean) {
        terminal.execute(SSHCommand(remote, data, obtainCommandOutput))
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