package net.milosvasic.factory.mail.component.docker.step

import net.milosvasic.factory.mail.component.docker.DockerOperation
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.remote.Connection
import net.milosvasic.factory.mail.remote.ssh.SSHCommand


class Volume(private val volumeDefinition: String) : DockerInstallationStep() {

    private val command = "echo '1 2 3'"

    override fun handleResult(result: OperationResult) {
        when (result.operation) {
            is SSHCommand -> {
                if (result.operation.command.endsWith(command)) {

                    finish(result.success, DockerOperation())
                }
            }
        }
    }

    @Synchronized
    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    override fun execute(vararg params: Connection) {
        super.execute(*params)
        connection?.execute(command)
    }
}