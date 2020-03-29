package net.milosvasic.factory.mail.component.packaging

import net.milosvasic.factory.mail.common.busy.Busy
import net.milosvasic.factory.mail.common.Notifying
import net.milosvasic.factory.mail.common.Subscription
import net.milosvasic.factory.mail.common.busy.BusyException
import net.milosvasic.factory.mail.component.Component
import net.milosvasic.factory.mail.component.Shutdown
import net.milosvasic.factory.mail.component.packaging.item.Group
import net.milosvasic.factory.mail.component.packaging.item.InstallationItem
import net.milosvasic.factory.mail.component.packaging.item.Package
import net.milosvasic.factory.mail.log
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
    private var iterator: Iterator<InstallationItem>? = null
    private var operationType: PackageManagerOperationType? = null
    private val subscribers = mutableSetOf<OperationResultListener>()

    private val listener = object : OperationResultListener {
        override fun onOperationPerformed(result: OperationResult) {
            when (result.operation) {
                is PackageManagerOperation -> {
                    if (result.success) {
                        tryNext()
                    } else {
                        unBusy(false)
                    }
                }
                else -> {
                    log.e("Unexpected operation result: $result")
                }
            }
        }
    }

    init {
        entryPoint.subscribe(listener)
    }

    @Synchronized
    @Throws(BusyException::class)
    open fun install(packages: List<Package>) {
        busy()
        iterator = packages.iterator()
        operationType = PackageManagerOperationType.PACKAGE_INSTALL
        tryNext()
    }

    @Synchronized
    @Throws(BusyException::class)
    open fun uninstall(packages: List<Package>) {
        busy()
        iterator = packages.iterator()
        operationType = PackageManagerOperationType.PACKAGE_UNINSTALL
        tryNext()
    }

    @Synchronized
    @Throws(BusyException::class)
    open fun groupInstall(groups: List<Group>) {
        busy()
        iterator = groups.iterator()
        operationType = PackageManagerOperationType.GROUP_INSTALL
        tryNext()
    }

    @Synchronized
    @Throws(BusyException::class)
    open fun groupUninstall(groups: List<Group>) {
        busy()
        iterator = groups.iterator()
        operationType = PackageManagerOperationType.GROUP_UNINSTALL
        tryNext()
    }

    private fun tryNext() {
        iterator?.let {
            operationType?.let { type ->
                if (it.hasNext()) {
                    when (val item = it.next()) {
                        is Package -> {
                            if (type == PackageManagerOperationType.PACKAGE_INSTALL) {
                                installPackage(item)
                            } else {
                                uninstallPackage(item)
                            }
                        }
                        is Group -> {
                            if (type == PackageManagerOperationType.GROUP_INSTALL) {
                                installGroup(item)
                            } else {
                                uninstallGroup(item)
                            }
                        }
                        else -> {
                            log.e("Install: unknown installation type: $item")
                        }
                    }
                } else {
                    operationType?.let {
                        unBusy(true)
                    }
                }
            }
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

    override fun shutdown() {
        entryPoint.unsubscribe(listener)
    }

    private fun installPackage(item: Package) {
        val cmd = "$installCommand ${item.value}"
        entryPoint.execute(cmd)
    }

    private fun uninstallPackage(item: Package) {
        val cmd = "$uninstallCommand ${item.value}"
        entryPoint.execute(cmd)
    }

    private fun installGroup(item: Group) {
        val cmd = "$groupInstallCommand ${item.value}"
        entryPoint.execute(cmd)
    }

    private fun uninstallGroup(item: Group) {
        val cmd = "$groupUninstallCommand ${item.value}"
        entryPoint.execute(cmd)
    }

    @Throws(BusyException::class)
    private fun busy() {
        if (busy.isBusy()) {
            throw BusyException()
        }
        busy.setBusy(true)
    }

    private fun unBusy(success: Boolean) {
        operationType?.let {
            val operation = PackageManagerOperation(it)
            val result = OperationResult(operation, success)
            notify(result)
            iterator = null
            operationType = null
            busy.setBusy(false)
        }
    }
}