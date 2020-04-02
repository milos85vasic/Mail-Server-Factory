package net.milosvasic.factory.mail.component.installer

import net.milosvasic.factory.mail.common.busy.BusyWorker
import net.milosvasic.factory.mail.component.Initialization
import net.milosvasic.factory.mail.component.installer.step.InstallationStep
import net.milosvasic.factory.mail.component.packaging.PackageInstaller
import net.milosvasic.factory.mail.component.packaging.PackageInstallerInitializationOperation
import net.milosvasic.factory.mail.configuration.SoftwareConfiguration
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.operation.OperationResultListener
import net.milosvasic.factory.mail.remote.ssh.SSH
import java.lang.IllegalArgumentException

class Installer(
    private val configuration: SoftwareConfiguration,
    entryPoint: SSH
) :
    BusyWorker<InstallationStep<*>>(entryPoint),
    Installation,
    Initialization {

    private val installer = PackageInstaller(entryPoint)

    private val listener = object : OperationResultListener {
        override fun onOperationPerformed(result: OperationResult) {

            when (result.operation) {
                is PackageInstallerInitializationOperation -> {

                    val installerInitializationOperation = InstallerInitializationOperation()
                    val operationResult = OperationResult(installerInitializationOperation, result.success)
                    notify(operationResult)
                }
            }
        }
    }

    @Synchronized
    @Throws(IllegalStateException::class)
    override fun initialize() {
        checkInitialized()
        busy()
        installer.subscribe(listener)
        installer.initialize()
    }

    @Synchronized
    @Throws(IllegalStateException::class)
    override fun terminate() {
        checkNotInitialized()
        installer.unsubscribe(listener)
        installer.terminate()
        super.terminate()
    }

    @Synchronized
    @Throws(IllegalStateException::class)
    override fun checkInitialized() {
        if (isInitialized()) {
            throw IllegalStateException("Installer has been already initialized")
        }
    }

    @Synchronized
    @Throws(IllegalStateException::class)
    override fun checkNotInitialized() {
        if (!isInitialized()) {
            throw IllegalStateException("Installer has not been initialized")
        }
    }

    @Synchronized
    override fun isInitialized() = installer.isInitialized()

    @Synchronized
    override fun install() {

        try {
            val steps = configuration.obtain(entryPoint.getRemoteOS().getType().osName)
            busy()
            iterator = steps.iterator()
            tryNext()
        } catch (e: IllegalArgumentException) {

            free(false)
        } catch (e: IllegalStateException) {

            free(false)
        }
    }

    @Synchronized
    @Throws(UnsupportedOperationException::class)
    override fun uninstall() {
        throw UnsupportedOperationException("Not implemented yet.")
    }

    override fun subscribe(what: OperationResultListener) {
        super.subscribe(what)
    }

    override fun unsubscribe(what: OperationResultListener) {
        super.unsubscribe(what)
    }

    override fun notify(data: OperationResult) {
        super.notify(data)
    }

    override fun busy() {
        super.busy()
    }

    override fun free() {
        super.free()
    }

    override fun free(success: Boolean) {
        super.free(success)
    }

    override fun tryNext() {
        TODO("Not yet implemented")
    }

    override fun onSuccessResult() {
        TODO("Not yet implemented")
    }

    override fun onFailedResult() {
        TODO("Not yet implemented")
    }

    override fun handleResult(result: OperationResult) {
        TODO("Not yet implemented")
    }

    override fun notify(success: Boolean) {
        TODO("Not yet implemented")
    }
}