package net.milosvasic.factory.mail.component.packageManagement

import net.milosvasic.factory.mail.common.busy.Busy
import net.milosvasic.factory.mail.common.Notifying
import net.milosvasic.factory.mail.common.Subscription
import net.milosvasic.factory.mail.common.busy.BusyException
import net.milosvasic.factory.mail.component.Component
import net.milosvasic.factory.mail.component.Shutdown
import net.milosvasic.factory.mail.component.packageManagement.item.Group
import net.milosvasic.factory.mail.component.packageManagement.item.Package
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.operation.OperationResultListener
import net.milosvasic.factory.mail.remote.ssh.SSH

abstract class PackageManager(private val entryPoint: SSH) :
    Component(),
    Subscription<OperationResultListener>,
    Notifying<OperationResult>,
    Shutdown {

    protected abstract val installCommand: String
    protected abstract val uninstallCommand: String
    protected abstract val groupInstallCommand: String
    protected abstract val groupUninstallCommand: String

    private val busy = Busy()
    private var iterator: Iterator<Package>? = null
    private val subscribers = mutableSetOf<OperationResultListener>()

    private val listener = object : OperationResultListener {
        override fun onOperationPerformed(result: OperationResult) {

            // TODO:
            notify(result)
        }
    }

    init {
        entryPoint.subscribe(listener)
    }

    @Synchronized
    @Throws(BusyException::class)
    open fun install(packages: List<Package>) {
        busy()
        var cmd = installCommand
        packages.forEach {
            cmd += " ${it.value}"
        }
        entryPoint.execute(cmd)
    }

    @Synchronized
    @Throws(BusyException::class)
    open fun uninstall(packages: List<Package>) {
        busy()
        var cmd = uninstallCommand
        packages.forEach {
            cmd += " ${it.value}"
        }
        entryPoint.execute(cmd)
    }

    @Synchronized
    @Throws(BusyException::class)
    open fun groupInstall(what: Group) {
        busy()
        val cmd = "$groupInstallCommand ${what.value}"
        entryPoint.execute(cmd)
    }

    @Synchronized
    @Throws(BusyException::class)
    open fun groupUninstall(what: Group) {
        busy()
        val cmd = "$groupUninstallCommand ${what.value}"
        entryPoint.execute(cmd)
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
        entryPoint.unsubscribe(listener)
    }

    @Throws(BusyException::class)
    private fun busy() {
        if (busy.isBusy()) {
            throw BusyException()
        }
        busy.setBusy(true)
    }
}