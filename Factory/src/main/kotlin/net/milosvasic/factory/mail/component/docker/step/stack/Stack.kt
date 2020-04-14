package net.milosvasic.factory.mail.component.docker.step.stack

import net.milosvasic.factory.mail.EMPTY
import net.milosvasic.factory.mail.component.docker.DockerCommand
import net.milosvasic.factory.mail.component.docker.DockerInstallationOperation
import net.milosvasic.factory.mail.component.docker.step.DockerInstallationStep
import net.milosvasic.factory.mail.configuration.ConfigurationManager
import net.milosvasic.factory.mail.operation.Command
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.remote.Connection
import net.milosvasic.factory.mail.terminal.Commands
import java.io.File


class Stack(private val composeYmlPath: String) : DockerInstallationStep() {

    private var dockerCompose = false
    private var command = String.EMPTY

    override fun handleResult(result: OperationResult) {

        when (result.operation) {
            is Command -> {
                if (command != String.EMPTY && result.operation.toExecute.endsWith(command)) {

                    if (dockerCompose) {
                        dockerCompose = false

                        val run = "run.sh"
                        val bashHead = "#!/bin/sh"
                        val path = getYmlPath()
                        val file = File(path)
                        val directory = file.parentFile
                        val shellScript = "$directory${File.separator}$run"
                        val chmod = "chmod +rx $shellScript"
                        val cmd = command
                        command = "${Commands.printf("$bashHead\\n$cmd")} > $shellScript; $chmod"
                        connection?.execute(command)
                    } else {

                        finish(result.success, DockerInstallationOperation())
                    }
                }
            }
        }
    }

    @Synchronized
    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    override fun execute(vararg params: Connection) {
        super.execute(*params)

        dockerCompose = true
        var args = String.EMPTY
        val variables = ConfigurationManager.getConfiguration().variables
        if (variables.isEmpty()) {
            args = String.EMPTY
        } else {
            variables.keys.forEach { key ->
                args += " $key=${variables[key]}"
            }
        }
        val path = getYmlPath()
        val flags = "-d --remove-orphans"
        command = "$args ${DockerCommand.COMPOSE.command} -f $path ${DockerCommand.UP.command} $flags"
        connection?.execute(command)
    }

    private fun getYmlPath(): String {
        var path = composeYmlPath
        if (!path.endsWith(".yml")) {
            path += File.separator + "docker-compose.yml"
        }
        return path
    }
}