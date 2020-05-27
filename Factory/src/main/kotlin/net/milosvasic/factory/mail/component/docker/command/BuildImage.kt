package net.milosvasic.factory.mail.component.docker.command

import net.milosvasic.factory.mail.component.docker.DockerCommand
import net.milosvasic.factory.mail.terminal.TerminalCommand

class BuildImage(path: String, name: String) : TerminalCommand(
        "${DockerCommand.DOCKER.obtain()} ${DockerCommand.BUILD.obtain()} --tag $name $path"
)