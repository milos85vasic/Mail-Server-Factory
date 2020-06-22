package net.milosvasic.factory.component.docker.step.stack

import net.milosvasic.factory.component.docker.DockerCommand
import net.milosvasic.factory.terminal.TerminalCommand
import net.milosvasic.factory.terminal.command.Commands

class CheckCommand(containerName: String, waitFor: Int = 3) : TerminalCommand(

        "${Commands.sleep} $waitFor && ${DockerCommand.DOCKER.obtain()} ${DockerCommand.PS.obtain()} -a --filter \"status=running\" | ${Commands.grep(containerName)}"
)