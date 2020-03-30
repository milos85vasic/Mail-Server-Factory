package net.milosvasic.factory.mail.component.packaging

import net.milosvasic.factory.mail.EMPTY
import net.milosvasic.factory.mail.component.Initialization
import net.milosvasic.factory.mail.component.packaging.item.Group
import net.milosvasic.factory.mail.component.packaging.item.Package
import net.milosvasic.factory.mail.component.packaging.item.Packages
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.remote.ssh.SSH
import net.milosvasic.factory.mail.terminal.Commands

class PackageInstaller(entryPoint: SSH) : PackageManager(entryPoint), Initialization {

    private var item: PackageManager? = null
    private var manager: PackageManager? = null
    private var iterator: Iterator<PackageManager>? = null
    private val supportedInstallers = LinkedHashSet<PackageManager>()

    init {
        supportedInstallers.addAll(listOf(Dnf(entryPoint), Yum(entryPoint), AptGet(entryPoint)))
    }

//    private val installerListener = object : OperationResultListener {
//        override fun onOperationPerformed(result: OperationResult) {
//            when (result.operation) {
//
//
//            }
//        }
//    }

    @Synchronized
    override fun initialize() {
        busy()
        iterator = supportedInstallers.iterator()
        tryNext()
    }

    override fun terminate() {
        // manager?.unsubscribe(installerListener)
        super.terminate()
    }

    override fun onSuccessResult() {
        item?.let {
            manager = it
            // manager?.subscribe(installerListener)
        }
        super.onSuccessResult()
    }

    override fun onFailedResult() {
        tryNext()
    }

    @Throws(IllegalStateException::class)
    override fun tryNext() {
        manager?.let {
            unBusy(true)
            return
        }
        if (iterator == null) {
            unBusy(false)
            return
        }
        iterator?.let {
            if (it.hasNext()) {
                item = it.next()
                item?.let { current ->
                    command = Commands.getApplicationInfo(current.applicationBinaryName)
                    entryPoint.execute(command)
                }
            } else {
                unBusy(false)
            }
        }
    }

    @Throws(IllegalStateException::class)
    override fun install(packages: List<Package>) {
        checkNotInitialized()
        manager?.install(packages)
    }

    @Throws(IllegalStateException::class)
    override fun install(packages: Packages) {
        checkNotInitialized()
        manager?.install(packages)
    }

    @Throws(IllegalStateException::class)
    override fun uninstall(packages: List<Package>) {
        checkNotInitialized()
        manager?.uninstall(packages)
    }

    @Throws(IllegalStateException::class)
    override fun groupInstall(groups: List<Group>) {
        checkNotInitialized()
        manager?.groupInstall(groups)
    }

    @Throws(IllegalStateException::class)
    override fun groupUninstall(groups: List<Group>) {
        checkNotInitialized()
        manager?.groupUninstall(groups)
    }

    override fun installCommand(): String {
        manager?.let {
            return it.installCommand()
        }
        return ""
    }

    override fun uninstallCommand(): String {
        manager?.let {
            return it.uninstallCommand()
        }
        return ""
    }

    override fun groupInstallCommand(): String {
        manager?.let {
            return it.groupInstallCommand()
        }
        return ""
    }

    override fun groupUninstallCommand(): String {
        manager?.let {
            return it.groupUninstallCommand()
        }
        return ""
    }

    override val applicationBinaryName: String
        get() = String.EMPTY

    @Throws(IllegalStateException::class)
    fun addSupportedPackageManager(packageManager: PackageManager) {
        checkInitialized()
        supportedInstallers.add(packageManager)
    }

    @Throws(IllegalStateException::class)
    private fun removeSupportedPackageManager(packageManager: PackageManager) {
        checkInitialized()
        supportedInstallers.remove(packageManager)
    }

    @Throws(IllegalStateException::class)
    private fun checkInitialized() {
        manager?.let {
            throw IllegalStateException("Package installer has been already initialized.")
        }
    }

    @Throws(IllegalStateException::class)
    private fun checkNotInitialized() {
        if (manager == null) {
            throw IllegalStateException("Package installer has not been initialized.")
        }
    }

    override fun notify(success: Boolean) {
        val operation = PackageInstallerInitializationOperation()
        val result = OperationResult(operation, success)
        notify(result)
    }
}