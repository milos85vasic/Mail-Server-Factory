package net.milosvasic.factory.mail.test

import net.milosvasic.factory.mail.component.installer.step.InstallationStepType

class ConditionStepFlowTest : SkipConditionStepFlowTest() {

    override fun name() = "Condition"

    override fun type() = InstallationStepType.CONDITION.type

    override fun expectedPositives() = 3

    override fun expectedNegatives() = 1

    override fun expectedTerminalCommandPositives() = 2

    override fun expectedTerminalCommandNegatives() = expectedPositives()
}