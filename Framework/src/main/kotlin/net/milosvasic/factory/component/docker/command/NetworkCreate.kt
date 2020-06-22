package net.milosvasic.factory.component.docker.command

import net.milosvasic.factory.component.docker.DockerCommand
import net.milosvasic.factory.terminal.TerminalCommand

class NetworkCreate(name: String) : TerminalCommand(

        "${DockerCommand.NETWORK_CREATE.obtain()} --driver bridge $name"
)