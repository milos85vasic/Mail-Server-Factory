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


class Stack(
        private val composeYmlPath: String,
        composeFileName: String = defaultComposeFileName,
        private val composeFileExtension: String = defaultComposeFileExtension
) : DockerInstallationStep() {

    companion object {

        private const val defaultComposeFileName: String = "docker-compose"
        private const val defaultComposeFileExtension: String = ".yml"
    }

    private var dockerCompose = false
    private var command = String.EMPTY
    private val flags = "-d --remove-orphans"
    private val composeFile = "$composeFileName$composeFileExtension"

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
                        val restartCmd = "sh stop.sh;\\nsh start.sh;"
                        val stopCmd = startCmd
                                .replace(DockerCommand.UP.obtain(), DockerCommand.DOWN.obtain())
                                .replace(flags, "")

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
        val configuration = ConfigurationManager.getConfiguration()
        val variables = configuration.variables
        if (variables.isEmpty()) {
            args = String.EMPTY
        } else {
            variables.keys.forEach { key ->
                args += " $key=${configuration.getVariableParsed(key)}"
            }
        }
        val path = getYmlPath()
        command = "$args ${DockerCommand.COMPOSE.obtain()} -f $path ${DockerCommand.UP.obtain()} $flags"
        connection?.execute(command)
    }

    private fun getYmlPath(): String {
        var path = composeYmlPath
        if (!path.endsWith(composeFileExtension)) {
            path += File.separator + composeFile
        }
        return path
    }

    private fun generate(command: String, script: String): String {

        val bashHead = "#!/bin/sh"
        val chmod = "chmod u+x $script"
        return "${Commands.printf("$bashHead\\n$command")} > $script; $chmod"
    }
}