package net.milosvasic.factory.component.packaging

import net.milosvasic.factory.common.busy.BusyWorker
import net.milosvasic.factory.component.packaging.item.Group
import net.milosvasic.factory.component.packaging.item.InstallationItem
import net.milosvasic.factory.component.packaging.item.Package
import net.milosvasic.factory.component.packaging.item.Packages
import net.milosvasic.factory.execution.flow.callback.FlowCallback
import net.milosvasic.factory.execution.flow.implementation.CommandFlow
import net.milosvasic.factory.operation.OperationResult
import net.milosvasic.factory.remote.Connection
import net.milosvasic.factory.terminal.TerminalCommand
import net.milosvasic.factory.terminal.command.PackageManagerCommand
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

    private val flowCallback = object : FlowCallback {

        override fun onFinish(success: Boolean) {
            if (success) {
                onSuccessResult()
            } else {
                onFailedResult()
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
                    throw IllegalArgumentException("All members must be of the same type")
                }
            }
        }
        busy()
        operationType = if (clazz == Group::class) {
            PackageManagerOperationType.GROUP_INSTALL
        } else {
            PackageManagerOperationType.PACKAGE_INSTALL
        }
        val flow = CommandFlow().width(entryPoint)
        items.forEach {
            val command = getCommand(it)
            flow.perform(command)
        }
        flow.onFinish(flowCallback).run()
    }

    @Synchronized
    @Throws(IllegalStateException::class, IllegalArgumentException::class)
    override fun install(packages: List<Package>) {
        busy()
        operationType = PackageManagerOperationType.PACKAGE_INSTALL
        val flow = CommandFlow().width(entryPoint)
        packages.forEach {
            val command = getCommand(it)
            flow.perform(command)
        }
        flow.onFinish(flowCallback).run()
    }

    @Synchronized
    @Throws(IllegalStateException::class, IllegalArgumentException::class)
    override fun install(packages: Packages) {
        busy()
        val list = listOf(Package(packages.value))
        operationType = PackageManagerOperationType.PACKAGE_INSTALL
        val flow = CommandFlow().width(entryPoint)
        list.forEach {
            val command = getCommand(it)
            flow.perform(command)
        }
        flow.onFinish(flowCallback).run()
    }

    @Synchronized
    @Throws(IllegalStateException::class, IllegalArgumentException::class)
    override fun uninstall(packages: List<Package>) {
        busy()
        operationType = PackageManagerOperationType.PACKAGE_UNINSTALL
        onFailedResult()
    }

    @Synchronized
    @Throws(IllegalStateException::class, IllegalArgumentException::class)
    override fun groupInstall(groups: List<Group>) {
        busy()
        operationType = PackageManagerOperationType.GROUP_INSTALL
        val flow = CommandFlow().width(entryPoint)
        groups.forEach {
            val command = getCommand(it)
            flow.perform(command)
        }
        flow.onFinish(flowCallback).run()
    }

    @Synchronized
    @Throws(IllegalStateException::class, IllegalArgumentException::class)
    override fun groupUninstall(groups: List<Group>) {
        busy()
        operationType = PackageManagerOperationType.GROUP_UNINSTALL
        onFailedResult()
    }

    @Throws(IllegalStateException::class, IllegalArgumentException::class)
    override fun onSuccessResult() {
        free(true)
    }

    override fun onFailedResult() {
        free(false)
    }

    @Synchronized
    override fun notify(success: Boolean) {
        val operation = PackageManagerOperation(operationType)
        val result = OperationResult(operation, success)
        notify(result)
    }

    @Throws(IllegalArgumentException::class)
    private fun getCommand(item: InstallationItem): TerminalCommand {
        return when (item) {
            is Package -> {
                PackageManagerCommand(installCommand(), item.value)
            }
            is Group -> {
                PackageManagerCommand(groupInstallCommand(), item.value)
            }
            else -> {
                throw IllegalArgumentException("Unsupported installation type: ${item::class.simpleName}")
            }
        }
    }
}