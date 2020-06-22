package net.milosvasic.factory.component.docker.step.stack

import net.milosvasic.factory.component.installer.step.condition.SkipCondition
import net.milosvasic.factory.terminal.TerminalCommand

open class SkipConditionCheck(
        containerName: String,
        checkCommand: TerminalCommand = CheckCommand(containerName)

) : SkipCondition(checkCommand)