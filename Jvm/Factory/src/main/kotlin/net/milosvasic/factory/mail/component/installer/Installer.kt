package net.milosvasic.factory.mail.component.installer

import net.milosvasic.factory.mail.EMPTY
import net.milosvasic.factory.mail.common.busy.BusyWorker
import net.milosvasic.factory.mail.component.Initialization
import net.milosvasic.factory.mail.component.installer.step.CommandInstallationStep
import net.milosvasic.factory.mail.component.installer.step.InstallationStep
import net.milosvasic.factory.mail.component.installer.step.PackageManagerInstallationStep
import net.milosvasic.factory.mail.component.packaging.PackageInstaller
import net.milosvasic.factory.mail.component.packaging.PackageInstallerInitializationOperation
import net.milosvasic.factory.mail.component.packaging.PackageManagerOperation
import net.milosvasic.factory.mail.configuration.SoftwareConfiguration
import net.milosvasic.factory.mail.log
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.operation.OperationResultListener
import net.milosvasic.factory.mail.remote.ssh.SSH
import net.milosvasic.factory.mail.remote.ssh.SSHCommand

class Installer(
    private val configuration: SoftwareConfiguration,
    entryPoint: SSH
) :
    BusyWorker<InstallationStep<*>>(entryPoint),
    Installation,
    Initialization {

    private var item: InstallationStep<*>? = null
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
                is PackageManagerOperation -> {

                    if (result.success) {

                        tryNext()
                    } else {

                        free(false)
                    }
                }
            }
        }
    }

    override fun handleResult(result: OperationResult) {
        when (result.operation) {
            is SSHCommand -> {

                if (command != String.EMPTY && result.operation.command.endsWith(command)) {
                    if (result.success) {
                        tryNext()
                    } else {
                        free(false)
                    }
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

            e.message?.let {
                log.e(it)
            }
            free(false)
        } catch (e: IllegalStateException) {

            e.message?.let {
                log.e(it)
            }
            free(false)
        }
    }

    @Synchronized
    @Throws(UnsupportedOperationException::class)
    override fun uninstall() {
        throw UnsupportedOperationException("Not implemented yet.")
    }

    @Throws(IllegalStateException::class, IllegalArgumentException::class)
    override fun tryNext() {

        if (iterator == null) {
            free(false)
            return
        }
        iterator?.let {
            if (it.hasNext()) {
                item = it.next()
                item?.let { current ->

                    when (current) {
                        is CommandInstallationStep -> {

                            command = current.command
                            current.execute(entryPoint)
                        }
                        is PackageManagerInstallationStep -> {
                            command = String.EMPTY
                            current.execute(installer)
                        }
                        else -> {
                            throw IllegalStateException("Unsupported installation step: $current")
                        }
                    }
                }
            } else {
                free(false)
            }
        }
    }

    override fun onSuccessResult() {
        tryNext()
    }

    override fun onFailedResult() {
        free(false)
    }

    override fun notify(success: Boolean) {
        val operation = InstallerOperation()
        val result = OperationResult(operation, success)
        notify(result)
    }
}