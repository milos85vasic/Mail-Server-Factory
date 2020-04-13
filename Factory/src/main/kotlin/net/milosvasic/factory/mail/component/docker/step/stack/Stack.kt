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

    private var ymlDeployed = false
    private var command = String.EMPTY

    override fun handleResult(result: OperationResult) {

        when (result.operation) {
            is Command -> {
                if (command != String.EMPTY && result.operation.toExecute.endsWith(command)) {

                    if (ymlDeployed) {
                        finish(result.success, DockerInstallationOperation())
                    } else {

                        ymlDeployed = true
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
                }
            }
        }
    }

    /*
    * FIXME: Create Scp installation step to deploy YML.
    *  Scp installation step should rely on SSH commands, not on local (host) system's SCP.
    * */
    @Synchronized
    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    override fun execute(vararg params: Connection) {
        super.execute(*params)
        val remote = connection?.getRemote()
        remote?.let {
            command = Commands.scp(
                    getYmlPath(),
                    getYmlPath(),
                    remote
            )
        }
        if (command == String.EMPTY) {
            throw IllegalArgumentException("No remote available for the connection")
        }
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