package net.milosvasic.factory.mail.component.packageManagement

import net.milosvasic.factory.mail.common.Notifying
import net.milosvasic.factory.mail.common.Subscription
import net.milosvasic.factory.mail.component.Component
import net.milosvasic.factory.mail.component.Shutdown
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

    private val subscribers = mutableSetOf<OperationResultListener>()

    private val listener = object : OperationResultListener {
        override fun onOperationPerformed(result: OperationResult) {
            notify(result)
        }
    }

    init {
        entryPoint.subscribe(listener)
    }

    open fun install(packages: List<String>) {
        var cmd = installCommand
        packages.forEach {
            cmd += " $it"
        }
        entryPoint.execute(cmd)
    }

    open fun uninstall(packages: List<String>) {
        var cmd = uninstallCommand
        packages.forEach {
            cmd += " $it"
        }
        entryPoint.execute(cmd)
    }

    open fun groupInstall(what: String) {
        val cmd = "$groupInstallCommand $what"
        entryPoint.execute(cmd)
    }

    open fun groupUninstall(what: String) {
        val cmd = "$groupUninstallCommand $what"
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
}