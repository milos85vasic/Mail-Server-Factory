package net.milosvasic.factory.mail.component

import net.milosvasic.factory.mail.component.packaging.item.InstallationItem
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.operation.OperationResultListener

class Installer(private val installations: List<SystemComponent>) : SystemComponent() {

    private val subscribers = mutableSetOf<OperationResultListener>()

    private val listener = object : OperationResultListener {
        override fun onOperationPerformed(result: OperationResult) {

            // TODO
            notify(result)
        }
    }

    override fun install() {
        installations.forEach {
            it.subscribe(listener)
            it.install()
        }
    }

    override fun uninstall() {
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

    @Throws(UnsupportedOperationException::class)
    override fun getDependencies(): List<List<InstallationItem>> {
        throw UnsupportedOperationException("Not supported by installer.")
    }
}