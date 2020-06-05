package net.milosvasic.factory.mail.component.docker.step.stack

import net.milosvasic.factory.mail.component.docker.DockerCommand
import net.milosvasic.factory.mail.terminal.TerminalCommand
import net.milosvasic.factory.mail.terminal.command.Commands

class CheckCommand(containerName: String, waitFor: Int = 3) : TerminalCommand(

        "${Commands.sleep} $waitFor && ${DockerCommand.DOCKER.obtain()} ${DockerCommand.PS.obtain()} -a --filter \"status=running\" | ${Commands.grep(containerName)}"
)