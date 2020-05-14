package net.milosvasic.factory.mail.component.installer.step.condition

class Condition(command: String) : SkipCondition(command) {

    override fun getOperation(result: Boolean) = ConditionOperation(result)
}