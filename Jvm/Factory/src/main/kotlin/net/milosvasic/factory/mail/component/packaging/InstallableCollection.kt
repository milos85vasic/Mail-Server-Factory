package net.milosvasic.factory.mail.component.packaging

import net.milosvasic.factory.mail.component.Shutdown
import net.milosvasic.factory.mail.component.SystemComponent
import net.milosvasic.factory.mail.component.packaging.item.Group
import net.milosvasic.factory.mail.component.packaging.item.Package
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.operation.OperationResultListener

class InstallableCollection(
    private val packages: List<Package>,
    private val groups: List<Group>,
    private val manager: PackageManager
) : SystemComponent(), Shutdown {

    private val subscribers = mutableSetOf<OperationResultListener>()

    private val listener = object : OperationResultListener {
        override fun onOperationPerformed(result: OperationResult) {

            // TODO
            notify(result)
        }
    }

    init {
        manager.subscribe(listener)
    }

    @Synchronized
    override fun install() {
        // TODO: Handle exception
        manager.install(packages)
        manager.groupInstall(groups)
    }

    @Synchronized
    override fun uninstall() {
        // TODO: Handle exception
        manager.uninstall(packages)
        manager.groupUninstall(groups)
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

    override fun shutdown() {
        manager.unsubscribe(listener)
    }
}