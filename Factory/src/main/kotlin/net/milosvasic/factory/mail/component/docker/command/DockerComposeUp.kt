package net.milosvasic.factory.mail.component.docker.command

import net.milosvasic.factory.mail.component.docker.DockerCommand
import net.milosvasic.factory.mail.terminal.TerminalCommand

class DockerComposeUp(path: String, flags: String) : TerminalCommand(

        "${DockerCommand.COMPOSE.obtain()} -f $path ${DockerCommand.UP.obtain()} $flags"
)