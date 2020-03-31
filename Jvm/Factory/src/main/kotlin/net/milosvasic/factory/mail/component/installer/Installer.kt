package net.milosvasic.factory.mail.component.installer

import net.milosvasic.factory.mail.common.Notifying
import net.milosvasic.factory.mail.common.Subscription
import net.milosvasic.factory.mail.common.busy.Busy
import net.milosvasic.factory.mail.component.Component
import net.milosvasic.factory.mail.component.SystemComponent
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.operation.OperationResultListener

class Installer(private val installations: List<SystemComponent>) :
    Component(),
    Installation,
    Subscription<OperationResultListener>,
    Notifying<OperationResult> {

    private val busy = Busy()
    private val subscribers = mutableSetOf<OperationResultListener>()

    private val listener = object : OperationResultListener {
        override fun onOperationPerformed(result: OperationResult) {

            // TODO
            notify(result)
        }
    }

    override val steps: List<InstallationStep>
        get() = listOf(

            // TODO: Steps
        )

    @Synchronized
    override fun install() {

        // TODO
        installations.forEach {
            it.subscribe(listener)
            it.install()
        }
    }

    @Synchronized
    override fun uninstall() {

        // TODO
        installations.forEach {
            it.subscribe(listener)
            it.uninstall()
        }
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