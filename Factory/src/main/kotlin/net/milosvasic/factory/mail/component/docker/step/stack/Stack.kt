package net.milosvasic.factory.mail.component.docker.step.stack

import net.milosvasic.factory.mail.EMPTY
import net.milosvasic.factory.mail.component.docker.DockerCommand
import net.milosvasic.factory.mail.component.docker.DockerInstallationOperation
import net.milosvasic.factory.mail.component.docker.step.DockerInstallationStep
import net.milosvasic.factory.mail.configuration.ConfigurationManager
import net.milosvasic.factory.mail.operation.Command
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.remote.Connection


class Stack(private val composeYmlPath: String) : DockerInstallationStep() {

    private var command = String.EMPTY

    override fun handleResult(result: OperationResult) {

        when (result.operation) {
            is Command -> {
                if (command != String.EMPTY && result.operation.toExecute.endsWith(command)) {

                    finish(result.success, DockerInstallationOperation())
                }
            }
        }
    }

    @Synchronized
    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    override fun execute(vararg params: Connection) {
        super.execute(*params)
        var args = String.EMPTY
        val variables = ConfigurationManager.getConfiguration().variables
        if (variables.isEmpty()) {
            args = String.EMPTY
        } else {
            variables.keys.forEach { key ->
                args += " $key=${variables[key]}"
            }
        }
        command = "$args ${DockerCommand.COMPOSE.command} -f $composeYmlPath/docker-compose.yml ${DockerCommand.UP.command}"
        connection?.execute(command)
    }
}