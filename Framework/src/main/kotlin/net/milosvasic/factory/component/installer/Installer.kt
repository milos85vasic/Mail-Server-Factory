package net.milosvasic.factory.component.installer

import net.milosvasic.factory.component.Toolkit
import net.milosvasic.factory.component.installer.recipe.registration.InstallerRecipeRegistrar
import net.milosvasic.factory.component.packaging.PackageInstaller
import net.milosvasic.factory.component.packaging.PackageInstallerInitializationOperation
import net.milosvasic.factory.component.packaging.PackageManager
import net.milosvasic.factory.component.packaging.PackageManagerSupport
import net.milosvasic.factory.operation.OperationResult
import net.milosvasic.factory.operation.OperationResultListener
import net.milosvasic.factory.remote.Connection

class Installer(entryPoint: Connection) : InstallerAbstract(entryPoint), PackageManagerSupport {

    private val installer = PackageInstaller(entryPoint)
    private val recipeRegistrar = InstallerRecipeRegistrar()

    init {
        recipeRegistrars.add(recipeRegistrar)
    }

    private val listener = object : OperationResultListener {
        override fun onOperationPerformed(result: OperationResult) {
            when (result.operation) {
                is PackageInstallerInitializationOperation -> {

                    free()
                    val installerInitializationOperation = InstallerInitializationOperation()
                    val operationResult = OperationResult(installerInitializationOperation, result.success)
                    notify(operationResult)
                }
            }
        }
    }

    override fun initialization() {
        installer.subscribe(listener)
        installer.initialize()
    }

    override fun termination() {
        installer.unsubscribe(listener)
        installer.terminate()
    }

    @Throws(IllegalStateException::class)
    override fun addSupportedPackageManager(packageManager: PackageManager) {
        installer.addSupportedPackageManager(packageManager)
    }

    @Throws(IllegalStateException::class)
    override fun removeSupportedPackageManager(packageManager: PackageManager) {
        installer.removeSupportedPackageManager(packageManager)
    }

    @Synchronized
    override fun isInitialized() = installer.isInitialized()

    override fun getNotifyOperation() = InstallerOperation()

    override fun getToolkit() = Toolkit(entryPoint, installer)

    override fun getEnvironmentName() = entryPoint.getRemoteOS().getType().osName
}