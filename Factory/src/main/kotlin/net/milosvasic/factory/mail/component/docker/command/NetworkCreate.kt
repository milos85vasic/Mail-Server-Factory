package net.milosvasic.factory.mail.component.docker.command

import net.milosvasic.factory.mail.component.docker.DockerCommand
import net.milosvasic.factory.mail.terminal.TerminalCommand

class NetworkCreate(name: String) : TerminalCommand(

        "${DockerCommand.NETWORK_CREATE.obtain()} --driver bridge $name"
)