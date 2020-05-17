package net.milosvasic.factory.mail.component.docker.step.stack

import net.milosvasic.factory.mail.component.docker.DockerCommand
import net.milosvasic.factory.mail.component.installer.step.CommandInstallationStep
import net.milosvasic.factory.mail.terminal.Commands

class Check(containerName: String) : CommandInstallationStep(

        "${DockerCommand.DOCKER.obtain()} ${DockerCommand.PS.obtain()} -a --filter \"status=running\" | ${Commands.grep(containerName)}"
)