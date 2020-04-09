package net.milosvasic.factory.mail.component.docker

import net.milosvasic.factory.mail.component.installer.InstallerAbstract
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.operation.OperationResultListener
import net.milosvasic.factory.mail.remote.Connection
import java.util.concurrent.atomic.AtomicBoolean

class Docker(entryPoint: Connection) : InstallerAbstract(entryPoint) {

    private val initialized = AtomicBoolean()

    private val listener = object : OperationResultListener {
        override fun onOperationPerformed(result: OperationResult) {

            when (result.operation) {

                // TODO:
            }
        }
    }

    @Synchronized
    @Throws(IllegalStateException::class)
    override fun initialize() {
        checkInitialized()
        busy()
        command = "${DockerCommand.DOCKER.command} ${DockerCommand.VERSION.command}"
        entryPoint.execute(command)
    }

    @Synchronized
    @Throws(IllegalStateException::class)
    override fun terminate() {
        checkNotInitialized()
        initialized.set(false)
        clearConfiguration()
        super.terminate()
    }

    @Synchronized
    override fun isInitialized() = initialized.get()

    override fun install() {
        TODO("Not yet implemented")
    }

    override fun uninstall() {
        TODO("Not yet implemented")
    }

    override fun tryNext() {

        // TODO: To be implemented.
    }

    override fun onSuccessResult() {

        // TODO: To be implemented.
    }

    override fun onFailedResult() {

        // TODO: To be implemented.
    }

    override fun handleResult(result: OperationResult) {

        // TODO: To be implemented.
    }

    override fun notify(success: Boolean) {

        // TODO: To be implemented.
    }

}