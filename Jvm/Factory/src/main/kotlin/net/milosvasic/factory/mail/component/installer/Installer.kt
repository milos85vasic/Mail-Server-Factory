package net.milosvasic.factory.mail.component.installer

import net.milosvasic.factory.mail.EMPTY
import net.milosvasic.factory.mail.common.busy.BusyException
import net.milosvasic.factory.mail.common.busy.BusyWorker
import net.milosvasic.factory.mail.component.Initialization
import net.milosvasic.factory.mail.component.installer.step.CommandInstallationStep
import net.milosvasic.factory.mail.component.installer.step.InstallationStep
import net.milosvasic.factory.mail.component.installer.step.PackageManagerInstallationStep
import net.milosvasic.factory.mail.component.installer.step.reboot.Reboot
import net.milosvasic.factory.mail.component.installer.step.reboot.RebootOperation
import net.milosvasic.factory.mail.component.packaging.PackageInstaller
import net.milosvasic.factory.mail.component.packaging.PackageInstallerInitializationOperation
import net.milosvasic.factory.mail.component.packaging.PackageManagerOperation
import net.milosvasic.factory.mail.configuration.SoftwareConfiguration
import net.milosvasic.factory.mail.log
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.operation.OperationResultListener
import net.milosvasic.factory.mail.remote.ssh.SSH
import net.milosvasic.factory.mail.remote.ssh.SSHCommand

class Installer(entryPoint: SSH) :
        BusyWorker<InstallationStep<*>>(entryPoint),
        Installation,
        Initialization {

    private var item: InstallationStep<*>? = null
    private val installer = PackageInstaller(entryPoint)
    private var configuration: SoftwareConfiguration? = null

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
                is RebootOperation -> {

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

                        try {
                            tryNext()
                        } catch (e: IllegalStateException) {

                            log.e(e)
                            free(false)
                        } catch (e: IllegalArgumentException) {

                            log.e(e)
                            free(false)
                        }
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
        clearConfiguration()
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

        if (configuration == null) {

            log.e("No configuration available. Please set configuration before installation.")
            free(false)
        } else {

            configuration?.let {
                try {
                    val steps = it.obtain(entryPoint.getRemoteOS().getType().osName)
                    busy()
                    iterator = steps.iterator()
                    tryNext()
                } catch (e: IllegalArgumentException) {

                    log.e(e)
                    free(false)
                } catch (e: IllegalStateException) {

                    log.e(e)
                    free(false)
                }
            }
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
                        is Reboot -> {

                            command = String.EMPTY
                            current.execute(entryPoint)
                        }
                        else -> {
                            throw IllegalStateException("Unsupported installation step: $current")
                        }
                    }
                }
            } else {

                free(true)
            }
        }
    }

    @Throws(IllegalStateException::class, IllegalArgumentException::class)
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

    @Synchronized
    @Throws(BusyException::class)
    fun setConfiguration(configuration: SoftwareConfiguration) {
        busy()
        this.configuration = configuration
        free()
    }

    @Synchronized
    @Throws(BusyException::class)
    fun clearConfiguration() {
        busy()
        configuration = null
        free()
    }
}