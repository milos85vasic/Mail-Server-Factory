package net.milosvasic.factory.mail.component.docker.step.stack

import net.milosvasic.factory.mail.component.installer.step.condition.SkipCondition
import net.milosvasic.factory.mail.terminal.TerminalCommand

open class SkipConditionCheck(
        containerName: String,
        checkCommand: TerminalCommand = CheckCommand(containerName)

) : SkipCondition(checkCommand)