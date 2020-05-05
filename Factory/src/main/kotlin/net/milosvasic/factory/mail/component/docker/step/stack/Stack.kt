package net.milosvasic.factory.mail.component.docker.step.stack

import net.milosvasic.factory.mail.EMPTY
import net.milosvasic.factory.mail.component.docker.DockerCommand
import net.milosvasic.factory.mail.component.docker.DockerInstallationOperation
import net.milosvasic.factory.mail.component.docker.step.DockerInstallationStep
import net.milosvasic.factory.mail.log
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.remote.Connection
import net.milosvasic.factory.mail.security.Permission
import net.milosvasic.factory.mail.security.Permissions
import net.milosvasic.factory.mail.terminal.Commands
import net.milosvasic.factory.mail.terminal.TerminalCommand
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
    private val operation = DockerInstallationOperation()
    private val composeFile = "$composeFileName$composeFileExtension"

    override fun handleResult(result: OperationResult) {

        when (result.operation) {
            is TerminalCommand -> {
                if (command != String.EMPTY && result.operation.command.endsWith(command)) {

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

                        try {
                            val ownershipAndPermissionsStart = getOwnershipAndPermissions(startShellScript)
                            val ownershipAndPermissionsStop = getOwnershipAndPermissions(stopShellScript)
                            val ownershipAndPermissionsRestart = getOwnershipAndPermissions(restartShellScript)

                            command = Commands.concatenate(
                                    startGenerate,
                                    stopGenerate,
                                    restartGenerate,
                                    ownershipAndPermissionsStart,
                                    ownershipAndPermissionsStop,
                                    ownershipAndPermissionsRestart
                            )
                            connection?.execute(TerminalCommand(command))
                        } catch (e: IllegalArgumentException) {

                            log.e(e)
                            finish(false, operation)
                        }
                    } else {

                        finish(result.success, operation)
                    }
                }
            }
        }
    }

    @Throws(IllegalArgumentException::class)
    private fun getOwnershipAndPermissions(script: String): String {
        val account = connection?.getRemote()?.account
                ?: throw IllegalArgumentException("No host for connection provided")
        val permissions = Permissions(Permission.ALL, Permission.NONE, Permission.NONE)
        return Commands.concatenate(
                Commands.chown(account, script),
                Commands.chgrp(account, script),
                Commands.chmod(script, permissions.obtain())
        )
    }

    @Synchronized
    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    override fun execute(vararg params: Connection) {
        super.execute(*params)
        dockerCompose = true
        val path = getYmlPath()
        command = "${DockerCommand.COMPOSE.obtain()} -f $path ${DockerCommand.UP.obtain()} $flags"
        connection?.execute(TerminalCommand(command))
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