package net.milosvasic.factory.mail.component.docker.step.stack

import net.milosvasic.factory.mail.component.docker.DockerCommand
import net.milosvasic.factory.mail.terminal.TerminalCommand
import net.milosvasic.factory.mail.terminal.command.Commands

class CheckCommand(containerName: String) : TerminalCommand(

        "${DockerCommand.DOCKER.obtain()} ${DockerCommand.PS.obtain()} -a --filter \"status=running\" | ${Commands.grep(containerName)}"
)