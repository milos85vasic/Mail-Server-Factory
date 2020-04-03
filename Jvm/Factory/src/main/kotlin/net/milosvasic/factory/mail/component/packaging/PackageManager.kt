package net.milosvasic.factory.mail.component.packaging

import net.milosvasic.factory.mail.common.busy.BusyWorker
import net.milosvasic.factory.mail.component.packaging.item.Group
import net.milosvasic.factory.mail.component.packaging.item.InstallationItem
import net.milosvasic.factory.mail.component.packaging.item.Packages
import net.milosvasic.factory.mail.component.packaging.item.Package
import net.milosvasic.factory.mail.log
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.remote.Connection
import net.milosvasic.factory.mail.remote.ssh.SSHCommand
import kotlin.reflect.KClass

abstract class PackageManager(entryPoint: Connection) :
    BusyWorker<InstallationItem>(entryPoint),
    PackageManagement<InstallationItem> {

    abstract val applicationBinaryName: String

    open fun installCommand() = "$applicationBinaryName install -y"
    open fun uninstallCommand() = "$applicationBinaryName remove -y"
    open fun groupInstallCommand() = "$applicationBinaryName groupinstall -y"
    open fun groupUninstallCommand() = "$applicationBinaryName groupremove -y"

    private var operationType = PackageManagerOperationType.UNKNOWN

    override fun handleResult(result: OperationResult) {
        when (result.operation) {
            is SSHCommand -> {
                val cmd = result.operation.command
                if (command == cmd) {
                    if (result.success) {

                        try {
                            onSuccessResult()
                        } catch (e: IllegalStateException) {
                            onFailedResult(e)
                        }
                    } else {
                        onFailedResult()
                    }
                }
            }
        }
    }

    @Synchronized
    @Throws(IllegalStateException::class, IllegalArgumentException::class)
    override fun install(vararg items: InstallationItem) {
        var clazz: KClass<*>? = null
        items.forEach {
            if (clazz == null) {
                clazz = it::class
            } else {
                if (clazz != it::class) {
                    throw IllegalArgumentException("All members must be of the same type.")
                }
            }
        }
        busy()
        iterator = items.iterator()
        operationType = if (clazz == Group::class) {
            PackageManagerOperationType.GROUP_INSTALL
        } else {
            PackageManagerOperationType.PACKAGE_INSTALL
        }
        tryNext()
    }

    @Synchronized
    @Throws(IllegalStateException::class)
    override fun install(packages: List<Package>) {
        busy()
        iterator = packages.iterator()
        operationType = PackageManagerOperationType.PACKAGE_INSTALL
        tryNext()
    }

    @Synchronized
    @Throws(IllegalStateException::class)
    override fun install(packages: Packages) {
        busy()
        val list = listOf(Package(packages.value))
        iterator = list.iterator()
        operationType = PackageManagerOperationType.PACKAGE_INSTALL
        tryNext()
    }

    @Synchronized
    @Throws(IllegalStateException::class)
    override fun uninstall(packages: List<Package>) {
        busy()
        iterator = packages.iterator()
        operationType = PackageManagerOperationType.PACKAGE_UNINSTALL
        tryNext()
    }

    @Synchronized
    @Throws(IllegalStateException::class)
    override fun groupInstall(groups: List<Group>) {
        busy()
        iterator = groups.iterator()
        operationType = PackageManagerOperationType.GROUP_INSTALL
        tryNext()
    }

    @Synchronized
    @Throws(IllegalStateException::class)
    override fun groupUninstall(groups: List<Group>) {
        busy()
        iterator = groups.iterator()
        operationType = PackageManagerOperationType.GROUP_UNINSTALL
        tryNext()
    }

    @Throws(IllegalStateException::class)
    override fun tryNext() {
        if (iterator == null) {
            free(false)
            return
        }
        if (operationType == PackageManagerOperationType.UNKNOWN) {
            free(false)
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
                free(true)
            }
        }
    }

    private fun installPackage(item: Package) {
        command = "${installCommand()} ${item.value}"
        entryPoint.execute(command)
    }

    private fun uninstallPackage(item: Package) {
        command = "${uninstallCommand()} ${item.value}"
        entryPoint.execute(command)
    }

    private fun installGroup(item: Group) {
        command = "${groupInstallCommand()} \"${item.value}\""
        entryPoint.execute(command)
    }

    private fun uninstallGroup(item: Group) {
        command = "${groupUninstallCommand()} \"${item.value}\""
        entryPoint.execute(command)
    }

    override fun notify(success: Boolean) {
        val operation = PackageManagerOperation(operationType)
        val result = OperationResult(operation, success)
        notify(result)
    }

    @Throws(IllegalStateException::class)
    override fun onSuccessResult() {
        tryNext()
    }

    override fun onFailedResult() {
        free(false)
    }
}