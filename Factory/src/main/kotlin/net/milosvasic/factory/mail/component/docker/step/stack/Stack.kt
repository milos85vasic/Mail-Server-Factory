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

                        val stop = "stop.sh"
                        val start = "start.sh"
                        val restart = "restart.sh"
                        val path = getYmlPath()
                        val file = File(path)
                        val directory = file.parentFile
                        val stopShellScript = "$directory${File.separator}$stop"
                        val startShellScript = "$directory${File.separator}$start"
                        val restartShellScript = "$directory${File.separator}$restart"
                        val startCmd = command
                        val compose = DockerCommand.COMPOSE.command
                        val stopCmd = "$compose -f $path down -v"
                        val restartCmd = "sh stop.sh;\\nsh start.sh;"

                        val startGenerate = generate(startCmd, startShellScript)
                        val stopGenerate = generate(stopCmd, stopShellScript)
                        val restartGenerate = generate(restartCmd, restartShellScript)

                        val builder = StringBuilder()
                                .append(startGenerate)
                                .append("; ").append(stopGenerate)
                                .append("; ").append(restartGenerate)

                        command = builder.toString()
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

    private fun generate(command: String, script: String): String {

        val bashHead = "#!/bin/sh"
        val chmod = "chmod +rx $script"
        return "${Commands.printf("$bashHead\\n$command")} > $script; $chmod"
    }
}