package net.milosvasic.factory.component.packaging

import net.milosvasic.factory.common.busy.BusyWorker
import net.milosvasic.factory.common.initialization.Initialization
import net.milosvasic.factory.common.initialization.Termination
import net.milosvasic.factory.component.Toolkit
import net.milosvasic.factory.component.installer.recipe.ConditionRecipe
import net.milosvasic.factory.component.installer.step.condition.SkipCondition
import net.milosvasic.factory.component.packaging.item.Group
import net.milosvasic.factory.component.packaging.item.InstallationItem
import net.milosvasic.factory.component.packaging.item.Package
import net.milosvasic.factory.component.packaging.item.Packages
import net.milosvasic.factory.execution.flow.callback.FlowCallback
import net.milosvasic.factory.execution.flow.implementation.InstallationStepFlow
import net.milosvasic.factory.log
import net.milosvasic.factory.operation.OperationResult
import net.milosvasic.factory.operation.OperationResultListener
import net.milosvasic.factory.remote.Connection
import net.milosvasic.factory.terminal.command.ApplicationInfoCommand

class PackageInstaller(entryPoint: Connection) :
        BusyWorker<PackageManager>(entryPoint),
        PackageManagement<PackageManager>,
        PackageManagerSupport,
        Initialization,
        Termination {

    private var manager: PackageManager? = null
    private val supportedPackageManagers = LinkedHashSet<PackageManager>()

    init {
        supportedPackageManagers.addAll(
                listOf(
                        Dnf(entryPoint),
                        Yum(entryPoint),
                        AptGet(entryPoint)
                )
        )
    }

    val flowCallback = object : FlowCallback {
        override fun onFinish(success: Boolean) {

            if (manager == null) {
                log.e("No package manager has been initialized")
            }
            notify(success && manager != null)
        }
    }

    private val operationCallback = object : OperationResultListener {
        override fun onOperationPerformed(result: OperationResult) {

            when (result.operation) {
                is ApplicationInfoCommand -> {

                    if (result.success) {
                        val app = result.operation.application
                        supportedPackageManagers.forEach { packageManager ->
                            if (app == packageManager.applicationBinaryName) {

                                val name = packageManager::class.simpleName
                                log.i("Package installer has been initialized: $name")
                                manager = packageManager
                                manager?.subscribe(this)
                                return@forEach
                            }
                        }
                    }
                }
                is PackageManagerOperation -> {
                    notify(result)
                }
            }
        }
    }

    @Synchronized
    @Throws(IllegalStateException::class, IllegalArgumentException::class)
    override fun initialize() {
        checkInitialized()
        busy()

        entryPoint.subscribe(operationCallback)
        val toolkit = Toolkit(entryPoint)
        val flow = InstallationStepFlow(toolkit)
                .onFinish(flowCallback)
                .registerRecipe(SkipCondition::class, ConditionRecipe::class)

        supportedPackageManagers.forEach { packageManager ->

            val command = getChallengeCommand(packageManager)
            val skipCondition = SkipCondition(command)
            flow.width(skipCondition)
        }
        flow.run()
    }

    @Synchronized
    @Throws(IllegalStateException::class)
    override fun terminate() {
        checkNotInitialized()
        manager?.unsubscribe(operationCallback)
        entryPoint.unsubscribe(operationCallback)
    }

    @Throws(IllegalStateException::class, IllegalArgumentException::class)
    override fun install(vararg items: InstallationItem) {

        checkNotInitialized()
        manager?.install(*items.toList().toTypedArray())
    }

    @Throws(IllegalStateException::class, IllegalArgumentException::class)
    override fun install(packages: List<Package>) {
        checkNotInitialized()
        manager?.install(packages)
    }

    @Throws(IllegalStateException::class, IllegalArgumentException::class)
    override fun install(packages: Packages) {
        checkNotInitialized()
        manager?.install(packages)
    }

    @Throws(IllegalStateException::class, IllegalArgumentException::class)
    override fun uninstall(packages: List<Package>) {
        checkNotInitialized()
        manager?.uninstall(packages)
    }

    @Throws(IllegalStateException::class, IllegalArgumentException::class)
    override fun groupInstall(groups: List<Group>) {
        checkNotInitialized()
        manager?.groupInstall(groups)
    }

    @Throws(IllegalStateException::class, IllegalArgumentException::class)
    override fun groupUninstall(groups: List<Group>) {
        checkNotInitialized()
        manager?.groupUninstall(groups)
    }

    @Throws(IllegalStateException::class)
    override fun addSupportedPackageManager(packageManager: PackageManager) {
        checkInitialized()
        supportedPackageManagers.add(packageManager)
    }

    @Throws(IllegalStateException::class)
    override fun removeSupportedPackageManager(packageManager: PackageManager) {
        checkInitialized()
        supportedPackageManagers.remove(packageManager)
    }

    @Synchronized
    override fun notify(success: Boolean) {
        val operation = PackageInstallerInitializationOperation()
        val result = OperationResult(operation, success)
        notify(result)
    }

    @Synchronized
    override fun isInitialized(): Boolean {
        manager?.let {
            return true
        }
        return false
    }

    @Synchronized
    @Throws(IllegalStateException::class)
    override fun checkInitialized() {
        manager?.let {
            throw IllegalStateException("Package installer has been already initialized")
        }
    }

    @Synchronized
    @Throws(IllegalStateException::class)
    override fun checkNotInitialized() {
        if (manager == null) {
            throw IllegalStateException("Package installer has not been initialized")
        }
    }

    override fun onSuccessResult() {
        free(true)
    }

    override fun onFailedResult() {
        free(false)
    }

    private fun getChallengeCommand(packageManager: PackageManager): ApplicationInfoCommand {

        val app = packageManager.applicationBinaryName
        return ApplicationInfoCommand(app)
    }
}