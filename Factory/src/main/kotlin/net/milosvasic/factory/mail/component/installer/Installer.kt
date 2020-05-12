package net.milosvasic.factory.mail.component.installer

import net.milosvasic.factory.mail.component.Toolkit
import net.milosvasic.factory.mail.component.packaging.PackageInstaller
import net.milosvasic.factory.mail.component.packaging.PackageInstallerInitializationOperation
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.operation.OperationResultListener
import net.milosvasic.factory.mail.remote.ssh.SSH

class Installer(entryPoint: SSH) : InstallerAbstract(entryPoint) {

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

    // TODO: Goes into recipes - Start
    /*


    @Throws(IllegalStateException::class, IllegalArgumentException::class)
    override fun handleNext(current: InstallationStep<*>): Boolean {
        if (!super.handleNext(current)) {

            when (current) {
                is PackageManagerInstallationStep -> {

                    command = String.EMPTY
                    current.execute(installer)
                    return true
                }
                else -> {
                    throw IllegalStateException("Unsupported installation step: $current")
                }
            }
        } else {

            return true
        }
    }
    */
    // TODO: Goes into recipes - End

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

    override fun getEnvironmentName() = entryPoint.getRemoteOS().getType().osName

    override fun getNotifyOperation() = InstallerOperation()

    override fun getToolkit() = Toolkit(entryPoint, installer)
}