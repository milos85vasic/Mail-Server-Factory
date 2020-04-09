package net.milosvasic.factory.mail.component.installer

import net.milosvasic.factory.mail.EMPTY
import net.milosvasic.factory.mail.component.installer.step.CommandInstallationStep
import net.milosvasic.factory.mail.component.installer.step.PackageManagerInstallationStep
import net.milosvasic.factory.mail.component.installer.step.RemoteOperationInstallationStep
import net.milosvasic.factory.mail.component.installer.step.condition.Condition
import net.milosvasic.factory.mail.component.installer.step.condition.ConditionOperation
import net.milosvasic.factory.mail.component.installer.step.reboot.Reboot
import net.milosvasic.factory.mail.component.installer.step.reboot.RebootOperation
import net.milosvasic.factory.mail.component.packaging.PackageInstaller
import net.milosvasic.factory.mail.component.packaging.PackageInstallerInitializationOperation
import net.milosvasic.factory.mail.component.packaging.PackageManagerOperation
import net.milosvasic.factory.mail.log
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.operation.OperationResultListener
import net.milosvasic.factory.mail.remote.ssh.SSH
import net.milosvasic.factory.mail.remote.ssh.SSHCommand

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
                is PackageManagerOperation -> {

                    if (result.success) {
                        tryNext()
                    } else {
                        free(false)
                    }
                }
                is RebootOperation -> {

                    unsubscribeFromItem(this)
                    if (result.success) {
                        tryNext()
                    } else {
                        free(false)
                    }
                }
                is ConditionOperation -> {

                    unsubscribeFromItem(this)
                    if (result.success) {
                        if (result.operation.result) {
                            free(true)
                        } else {
                            tryNext()
                        }
                    } else {
                        if (result.operation.result) {
                            tryNext()
                        } else {
                            free(false)
                        }
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
    override fun isInitialized() = installer.isInitialized()

    override fun getEnvironmentName() = entryPoint.getRemoteOS().getType().osName

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
                        is Condition -> {

                            command = String.EMPTY
                            current.subscribe(listener)
                            current.execute(entryPoint)
                        }
                        is Reboot -> {

                            if (entryPoint is SSH) {
                                command = String.EMPTY
                                current.subscribe(listener)
                                current.execute(entryPoint)
                            } else {

                                val clazz = entryPoint::class.simpleName
                                val msg = "Reboot installation step does not support $clazz connection"
                                throw IllegalArgumentException(msg)
                            }
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

    @Synchronized
    override fun notify(success: Boolean) {
        val operation = InstallerOperation()
        val result = OperationResult(operation, success)
        notify(result)
    }

    private fun unsubscribeFromItem(listener: OperationResultListener) {
        item?.let { current ->
            when (current) {
                is RemoteOperationInstallationStep -> {
                    current.unsubscribe(listener)
                }
            }
        }
    }
}