package net.milosvasic.factory.mail.remote.ssh

import net.milosvasic.factory.mail.common.Notifying
import net.milosvasic.factory.mail.common.Subscription
import net.milosvasic.factory.mail.remote.Connection
import net.milosvasic.factory.mail.remote.operation.OperationResult
import net.milosvasic.factory.mail.remote.operation.OperationResultListener
import net.milosvasic.factory.mail.remote.operation.TestOperation

class SSH(private val remote: SSHRemote) :
    Connection<SSHRemote>(remote),
    Subscription<OperationResultListener>,
    Notifying<OperationResult>
{

    private val subscribers = mutableSetOf<OperationResultListener>()

    override fun test() {


        notify(OperationResult(TestOperation(), false))
    }

    override fun subscribe(what: OperationResultListener) {
        subscribers.add(what)
    }

    override fun unsubscribe(what: OperationResultListener) {
        subscribers.remove(what)
    }

    override fun notify(data: OperationResult) {
        val iterator = subscribers.iterator()
        while (iterator.hasNext()) {
            val listener = iterator.next()
            listener.onOperationPerformed(data)
        }
    }
}