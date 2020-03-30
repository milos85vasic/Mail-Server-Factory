package net.milosvasic.factory.mail.component.packaging

import net.milosvasic.factory.mail.common.busy.Busy
import net.milosvasic.factory.mail.common.Notifying
import net.milosvasic.factory.mail.common.Subscription
import net.milosvasic.factory.mail.common.busy.BusyException
import net.milosvasic.factory.mail.component.Component
import net.milosvasic.factory.mail.component.Termination
import net.milosvasic.factory.mail.component.packaging.item.Group
import net.milosvasic.factory.mail.component.packaging.item.InstallationItem
import net.milosvasic.factory.mail.component.packaging.item.Packages
import net.milosvasic.factory.mail.component.packaging.item.Package
import net.milosvasic.factory.mail.log
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.operation.OperationResultListener
import net.milosvasic.factory.mail.remote.ssh.SSH
import net.milosvasic.factory.mail.remote.ssh.SSHCommand

abstract class PackageManager(private val entryPoint: SSH) :
    Component(),
    Subscription<OperationResultListener>,
    Notifying<OperationResult>,
    Termination {

    abstract val installCommand: String
    abstract val uninstallCommand: String
    abstract val groupInstallCommand: String
    abstract val groupUninstallCommand: String

    private val busy = Busy()
    private var command = ""
    private var iterator: Iterator<InstallationItem>? = null
    private var operationType = PackageManagerOperationType.UNKNOWN
    private val subscribers = mutableSetOf<OperationResultListener>()

    private val listener = object : OperationResultListener {
        override fun onOperationPerformed(result: OperationResult) {
            when (result.operation) {
                is SSHCommand -> {
                    val cmd = result.operation.command
                    if (command == cmd) {
                        if (result.success) {
                            tryNext()
                        } else {
                            unBusy(false)
                        }
                    }
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
    open fun install(packages: Packages) {
        busy()
        val list = listOf(Package(packages.value))
        iterator = list.iterator()
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

    @Throws(IllegalStateException::class)
    private fun tryNext() {
        if (iterator == null) {
            unBusy(false)
            return
        }
        if (operationType == PackageManagerOperationType.UNKNOWN) {
            unBusy(false)
            return
        }
        iterator?.let {
            if (it.hasNext()) {
                when (val item = it.next()) {
                    is Package -> {
                        if (operationType == PackageManagerOperationType.PACKAGE_INSTALL) {
                            installPackage(item)
                        } else {
                            uninstallPackage(item)
                        }
                    }
                    is Group -> {
                        if (operationType == PackageManagerOperationType.GROUP_INSTALL) {
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
                unBusy(true)
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

    override fun terminate() {
        log.v("Shutting down: $this")
        entryPoint.unsubscribe(listener)
    }

    private fun installPackage(item: Package) {
        command = "$installCommand ${item.value}"
        entryPoint.execute(command)
    }

    private fun uninstallPackage(item: Package) {
        command = "$uninstallCommand ${item.value}"
        entryPoint.execute(command)
    }

    private fun installGroup(item: Group) {
        command = "$groupInstallCommand ${item.value}"
        entryPoint.execute(command)
    }

    private fun uninstallGroup(item: Group) {
        command = "$groupUninstallCommand ${item.value}"
        entryPoint.execute(command)
    }

    @Throws(BusyException::class)
    private fun busy() {
        if (busy.isBusy()) {
            throw BusyException()
        }
        busy.setBusy(true)
    }

    private fun unBusy(success: Boolean) {
        val operation = PackageManagerOperation(operationType)
        val result = OperationResult(operation, success)
        notify(result)
        command = ""
        iterator = null
        operationType = PackageManagerOperationType.UNKNOWN
        busy.setBusy(false)
    }
}