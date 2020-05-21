package net.milosvasic.factory.mail.component.installer.step.condition

import net.milosvasic.factory.mail.terminal.TerminalCommand

open class Condition(command: TerminalCommand) : SkipCondition(command) {

    override fun getOperation() = ConditionOperation(exception == null)
}