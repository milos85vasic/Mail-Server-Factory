package net.milosvasic.factory.mail.component.docker.step.stack

import net.milosvasic.factory.mail.component.docker.DockerCommand
import net.milosvasic.factory.mail.component.docker.DockerInstallationOperation
import net.milosvasic.factory.mail.component.docker.step.DockerInstallationStep
import net.milosvasic.factory.mail.execution.flow.implementation.CommandFlow
import net.milosvasic.factory.mail.security.Permission
import net.milosvasic.factory.mail.security.Permissions
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

    private val flags = "-d --remove-orphans"
    private val composeFile = "$composeFileName$composeFileExtension"


    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    override fun getFlow(): CommandFlow {
        connection?.let { conn ->

            val path = getYmlPath()
            val command = "${DockerCommand.COMPOSE.obtain()} -f $path ${DockerCommand.UP.obtain()} $flags"

            return CommandFlow()
                    .width(conn)
                    .perform(command)
                    .perform(getCompletionCommand(command))
        }
        throw IllegalArgumentException("No connection provided")
    }

    override fun getOperation() = DockerInstallationOperation()

    private fun getCompletionCommand(command: String): String {

        val stop = "stop.sh"
        val start = "start.sh"
        val restart = "restart.sh"
        val path = getYmlPath()
        val file = File(path)
        val directory = file.parentFile
        val stopShellScript = "$directory${File.separator}$stop"
        val startShellScript = "$directory${File.separator}$start"
        val restartShellScript = "$directory${File.separator}$restart"
        val restartCmd = "sh stop.sh;\\nsh start.sh;"
        val stopCmd = command
                .replace(DockerCommand.UP.obtain(), DockerCommand.DOWN.obtain())
                .replace(flags, "")

        val startGenerate = generate(command, startShellScript)
        val stopGenerate = generate(stopCmd, stopShellScript)
        val restartGenerate = generate(restartCmd, restartShellScript)

        val ownershipAndPermissionsStart = getOwnershipAndPermissions(startShellScript)
        val ownershipAndPermissionsStop = getOwnershipAndPermissions(stopShellScript)
        val ownershipAndPermissionsRestart = getOwnershipAndPermissions(restartShellScript)

        return Commands.concatenate(
                startGenerate,
                stopGenerate,
                restartGenerate,
                ownershipAndPermissionsStart,
                ownershipAndPermissionsStop,
                ownershipAndPermissionsRestart
        )
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