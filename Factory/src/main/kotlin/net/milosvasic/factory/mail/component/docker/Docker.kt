package net.milosvasic.factory.mail.component.docker

import net.milosvasic.factory.mail.component.installer.InstallerAbstract
import net.milosvasic.factory.mail.component.installer.step.InstallationStep
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.remote.Connection
import java.util.concurrent.atomic.AtomicBoolean

class Docker(entryPoint: Connection) : InstallerAbstract(entryPoint) {

    private val initialized = AtomicBoolean()

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
        if(!super.handleNext(current)){

            // when (current) {

                // TODO: Support docker installation steps

               // else -> {
                    throw IllegalStateException("Unsupported installation step: $current")
               // }
            // }
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