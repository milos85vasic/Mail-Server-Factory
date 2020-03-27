package net.milosvasic.factory.mail.terminal

import net.milosvasic.factory.mail.common.Execution
import net.milosvasic.factory.mail.common.Notifying
import net.milosvasic.factory.mail.common.Subscription
import net.milosvasic.factory.mail.remote.operation.OperationResult
import net.milosvasic.factory.mail.remote.operation.OperationResultListener

class Terminal :
    Execution<Command>,
    Subscription<OperationResultListener>,
    Notifying<OperationResult>
{

    private val runtime = Runtime.getRuntime()
    private val subscribers = mutableSetOf<OperationResultListener>()

    override fun execute(what: Command) {


    }

    override fun subscribe(what: OperationResultListener) {
        subscribers.add(what)
    }

    override fun unsubscribe(what: OperationResultListener) {
        subscribers.remove(what)
    }

    override fun notify(data: OperationResult) {
        subscribers.forEach {
            it.onOperationPerformed(data)
        }
    }
}