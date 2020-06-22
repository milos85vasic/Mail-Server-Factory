package net.milosvasic.factory.test

import net.milosvasic.factory.component.installer.step.InstallationStepType

class ConditionStepFlowTest : SkipConditionStepFlowTest() {

    override fun name() = "Condition"

    override fun type() = InstallationStepType.CONDITION.type

    override fun expectedPositives() = 3

    override fun expectedNegatives() = 1

    override fun expectedTerminalCommandPositives() = expectedPositives()

    override fun expectedTerminalCommandNegatives() = 2
}