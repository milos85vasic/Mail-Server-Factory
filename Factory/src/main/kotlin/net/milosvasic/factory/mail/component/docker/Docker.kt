package net.milosvasic.factory.mail.component.docker

import net.milosvasic.factory.mail.EMPTY
import net.milosvasic.factory.mail.component.docker.step.Volume
import net.milosvasic.factory.mail.component.installer.InstallerAbstract
import net.milosvasic.factory.mail.component.installer.step.InstallationStep
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.operation.OperationResultListener
import net.milosvasic.factory.mail.remote.Connection
import java.util.concurrent.atomic.AtomicBoolean

class Docker(entryPoint: Connection) : InstallerAbstract(entryPoint) {

    private val initialized = AtomicBoolean()

    private val listener = object : OperationResultListener {
        override fun onOperationPerformed(result: OperationResult) {

            try {
                handleResult(result)
            } catch (e: IllegalStateException) {

                onFailedResult(e)
            } catch (e: IllegalArgumentException) {

                onFailedResult(e)
            }
        }
    }

    override fun handleResult(result: OperationResult) {
        super.handleResult(result)
        when (result.operation) {

            is DockerInstallationOperation -> {

                unsubscribeFromItem(listener)
                if (result.success) {
                    tryNext()
                } else {
                    free(false)
                }
            }
        }
    }

    override fun onCommandPerformed(result: OperationResult) {
        if (initialized.get()) {
            super.onCommandPerformed(result)
        } else {
            if (result.success) {

                initialized.set(true)
                free()
                val dockerInitializationOperation = DockerInitializationOperation()
                val operationResult = OperationResult(dockerInitializationOperation, result.success)
                notify(operationResult)
            } else {

                free(false)
            }
        }
    }

    @Throws(IllegalStateException::class, IllegalArgumentException::class)
    override fun handleNext(current: InstallationStep<*>): Boolean {
        if (!super.handleNext(current)) {

            when (current) {
                is Volume -> {

                    command = String.EMPTY
                    current.subscribe(listener)
                    current.execute(entryPoint)
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
    @Throws(IllegalStateException::class)
    override fun initialize() {
        super.initialize()
        command = "${DockerCommand.DOCKER.command} ${DockerCommand.VERSION.command}"
        entryPoint.execute(command)
    }

    @Synchronized
    @Throws(IllegalStateException::class)
    override fun terminate() {
        super.terminate()
        initialized.set(false)
    }

    @Synchronized
    override fun isInitialized() = initialized.get()

    override fun getEnvironmentName() = "Docker"

    override fun getNotifyOperation() = DockerOperation()
}