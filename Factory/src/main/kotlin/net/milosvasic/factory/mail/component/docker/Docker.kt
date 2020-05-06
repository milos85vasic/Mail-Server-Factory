package net.milosvasic.factory.mail.component.docker

import net.milosvasic.factory.mail.EMPTY
import net.milosvasic.factory.mail.component.docker.step.stack.Stack
import net.milosvasic.factory.mail.component.docker.step.volume.Volume
import net.milosvasic.factory.mail.component.installer.InstallerAbstract
import net.milosvasic.factory.mail.component.installer.step.InstallationStep
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.operation.OperationResultListener
import net.milosvasic.factory.mail.remote.Connection
import net.milosvasic.factory.mail.terminal.TerminalCommand
import java.util.concurrent.atomic.AtomicBoolean

class Docker(entryPoint: Connection) : InstallerAbstract(entryPoint) {

    private val initialized = AtomicBoolean()

    private val listener = object : OperationResultListener {
        override fun onOperationPerformed(result: OperationResult) {

            handleResultAndCatch(result)
        }
    }

    @Throws(IllegalStateException::class, IllegalArgumentException::class)
    override fun handleResult(result: OperationResult) {
        super.handleResult(result)
        when (result.operation) {

            is DockerInstallationOperation -> {
                unsubscribeFromItem(listener)
                checkResultAndTryNext(result)
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
                is Stack -> {

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
    @Throws(IllegalStateException::class, IllegalArgumentException::class)
    override fun initialize() {
        super.initialize()
        command = "${DockerCommand.DOCKER.obtain()} ${DockerCommand.VERSION.obtain()}"
        entryPoint.execute(TerminalCommand(command))
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