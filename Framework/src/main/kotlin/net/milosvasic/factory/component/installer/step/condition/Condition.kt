package net.milosvasic.factory.component.installer.step.condition

import net.milosvasic.factory.terminal.TerminalCommand

open class Condition(command: TerminalCommand) : SkipCondition(command) {

    override fun getOperation() = ConditionOperation(exception == null)
}