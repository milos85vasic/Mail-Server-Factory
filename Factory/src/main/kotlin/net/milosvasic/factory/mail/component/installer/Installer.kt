package net.milosvasic.factory.mail.component.installer

import net.milosvasic.factory.mail.component.Toolkit
import net.milosvasic.factory.mail.component.installer.recipe.PackageManagerInstallationStepRecipe
import net.milosvasic.factory.mail.component.installer.step.InstallationStep
import net.milosvasic.factory.mail.component.installer.step.PackageManagerInstallationStep
import net.milosvasic.factory.mail.component.packaging.PackageInstaller
import net.milosvasic.factory.mail.component.packaging.PackageInstallerInitializationOperation
import net.milosvasic.factory.mail.execution.flow.implementation.InstallationStepFlow
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.operation.OperationResultListener
import net.milosvasic.factory.mail.remote.Connection

class Installer(entryPoint: Connection) : InstallerAbstract(entryPoint) {

    private val installer = PackageInstaller(entryPoint)

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

    @Throws(IllegalArgumentException::class)
    override fun registerRecipes(step: InstallationStep<*>, flow: InstallationStepFlow) {
        super.registerRecipes(step, flow)
        when (step) {
            is PackageManagerInstallationStep -> {
                flow.registerRecipe(
                        PackageManagerInstallationStep::class,
                        PackageManagerInstallationStepRecipe::class
                )
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

    @Synchronized
    override fun isInitialized() = installer.isInitialized()

    override fun getNotifyOperation() = InstallerOperation()

    override fun getToolkit() = Toolkit(entryPoint, installer)

    override fun getEnvironmentName() = entryPoint.getRemoteOS().getType().osName
}