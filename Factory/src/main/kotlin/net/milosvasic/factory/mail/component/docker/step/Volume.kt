package net.milosvasic.factory.mail.component.docker.step

import net.milosvasic.factory.mail.component.docker.DockerInstallationOperation
import net.milosvasic.factory.mail.operation.Command
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.remote.Connection


class Volume(private val volumeDefinition: String) : DockerInstallationStep() {

    private val command = "echo 'volume: $volumeDefinition'"

    override fun handleResult(result: OperationResult) {
        when (result.operation) {
            is Command -> {
                if (result.operation.toExecute.endsWith(command)) {

                    finish(result.success, DockerInstallationOperation())
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