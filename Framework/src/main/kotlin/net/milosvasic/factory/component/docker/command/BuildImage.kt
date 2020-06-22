package net.milosvasic.factory.component.docker.command

import net.milosvasic.factory.component.docker.DockerCommand
import net.milosvasic.factory.terminal.TerminalCommand

class BuildImage(path: String, name: String) : TerminalCommand(
        "${DockerCommand.DOCKER.obtain()} ${DockerCommand.BUILD.obtain()} --tag $name $path"
)