package net.milosvasic.factory.mail.component.docker.step.stack

import net.milosvasic.factory.mail.component.docker.DockerCommand
import net.milosvasic.factory.mail.component.installer.step.condition.SkipCondition
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.terminal.Commands
import net.milosvasic.factory.mail.terminal.TerminalCommand

open class SkipConditionCheck(
        containerName: String,
        checkCommand: String = "${DockerCommand.DOCKER.obtain()} ${DockerCommand.PS.obtain()} -a --filter \"status=running\" | ${Commands.grep(containerName)}"

) : SkipCondition(checkCommand)