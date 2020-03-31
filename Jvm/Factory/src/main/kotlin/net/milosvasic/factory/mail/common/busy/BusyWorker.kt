package net.milosvasic.factory.mail.common.busy

import net.milosvasic.factory.mail.common.Notifying
import net.milosvasic.factory.mail.common.Subscription
import net.milosvasic.factory.mail.component.Component
import net.milosvasic.factory.mail.component.Termination
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.operation.OperationResultListener

abstract class BusyWorker :
    Component(),
    Subscription<OperationResultListener>,
    Notifying<OperationResult>,
    Termination {

    protected val busy = Busy()
    private val subscribers = mutableSetOf<OperationResultListener>()

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