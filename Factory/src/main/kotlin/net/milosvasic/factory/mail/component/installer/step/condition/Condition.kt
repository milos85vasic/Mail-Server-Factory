package net.milosvasic.factory.mail.component.installer.step.condition

open class Condition(command: String) : SkipCondition(command) {

    override fun getOperation() = ConditionOperation(exception == null)
}