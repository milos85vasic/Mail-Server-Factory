package net.milosvasic.factory.mail.component.docker.step.stack

import net.milosvasic.factory.mail.component.installer.step.CommandInstallationStep
import net.milosvasic.factory.mail.terminal.TerminalCommand

open class Check(
        containerName: String,
        checkCommand: TerminalCommand = CheckCommand(containerName)

) : CommandInstallationStep(checkCommand)