package net.milosvasic.factory.mail.component.docker

import net.milosvasic.factory.mail.EMPTY
import net.milosvasic.factory.mail.component.Toolkit
import net.milosvasic.factory.mail.component.installer.InstallerAbstract
import net.milosvasic.factory.mail.remote.Connection
import net.milosvasic.factory.mail.terminal.TerminalCommand
import java.util.concurrent.atomic.AtomicBoolean

class Docker(entryPoint: Connection) : InstallerAbstract(entryPoint) {

    private var command = String.EMPTY
    private val initialized = AtomicBoolean()

    // TODO: Goes into recipes - Start
    /*
    private val listener = object : OperationResultListener {
        override fun onOperationPerformed(result: OperationResult) {

            handleResultAndCatch(result)
        }
    }

    @Throws(IllegalStateException::class, IllegalArgumentException::class)
    override fun handleResult(result: OperationResult) {
        super.handleResult(result)
        when (result.operation) {

            is CheckOperation -> {

                unsubscribeFromItem(listener)
                //checkResultAndTryNext(result)
            }
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
     */
    // TODO: Goes into recipes - End

    override fun initialization() {
        command = "${DockerCommand.DOCKER.obtain()} ${DockerCommand.VERSION.obtain()}"
        entryPoint.execute(TerminalCommand(command))
    }

    override fun termination() {
        initialized.set(false)
    }

    @Synchronized
    override fun isInitialized() = initialized.get()

    override fun getEnvironmentName() = "Docker"

    override fun getNotifyOperation() = DockerOperation()

    override fun getToolkit() = Toolkit(entryPoint)
}