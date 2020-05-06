package net.milosvasic.factory.mail.component.installer

import net.milosvasic.factory.mail.EMPTY
import net.milosvasic.factory.mail.component.installer.step.InstallationStep
import net.milosvasic.factory.mail.component.installer.step.PackageManagerInstallationStep
import net.milosvasic.factory.mail.component.packaging.PackageInstaller
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.operation.OperationResultListener
import net.milosvasic.factory.mail.remote.ssh.SSH

class Installer(entryPoint: SSH) : InstallerAbstract(entryPoint) {

    private val installer = PackageInstaller(entryPoint)

    private val listener = object : OperationResultListener {
        override fun onOperationPerformed(result: OperationResult) {

            handleResultAndCatch(result)
        }
    }

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

    @Synchronized
    @Throws(IllegalStateException::class, IllegalArgumentException::class)
    override fun initialize() {
        super.initialize()
        installer.subscribe(listener)
        installer.initialize()
    }

    @Synchronized
    @Throws(IllegalStateException::class)
    override fun terminate() {
        super.terminate()
        installer.unsubscribe(listener)
        installer.terminate()
    }

    @Synchronized
    override fun isInitialized() = installer.isInitialized()

    override fun getEnvironmentName() = entryPoint.getRemoteOS().getType().osName

    override fun getNotifyOperation() = InstallerOperation()
}