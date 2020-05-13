package net.milosvasic.factory.mail.test

import net.milosvasic.factory.mail.component.Toolkit
import net.milosvasic.factory.mail.component.installer.recipe.CommandInstallationStepRecipe
import net.milosvasic.factory.mail.component.installer.recipe.ConditionRecipe
import net.milosvasic.factory.mail.component.installer.step.CommandInstallationStep
import net.milosvasic.factory.mail.component.installer.step.InstallationStepFactory
import net.milosvasic.factory.mail.component.installer.step.InstallationStepType
import net.milosvasic.factory.mail.component.installer.step.condition.Condition
import net.milosvasic.factory.mail.configuration.InstallationStepDefinition
import net.milosvasic.factory.mail.execution.flow.callback.FlowCallback
import net.milosvasic.factory.mail.execution.flow.implementation.InstallationStepFlow
import net.milosvasic.factory.mail.log
import net.milosvasic.factory.mail.test.implementation.StubConnection
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ConditionStepFlowTest : BaseTest() {

    @Test
    fun testConditionStepFlow() {
        initLogging()
        log.i("Condition step flow test started")

        var failed = 1
        var finished = 0
        val connection = StubConnection()
        val toolkit = Toolkit(connection)
        val factory = InstallationStepFactory()

        val flowCallback = object : FlowCallback<String> {

            override fun onFinish(success: Boolean, message: String, data: String?) {
                if (success) {
                    finished++
                } else {
                    failed++
                }
            }
        }

        var positiveFlow = InstallationStepFlow(toolkit)
        var definitions = getDefinitions(false, true)
        definitions.forEach { definition ->
            val installationStep = factory.obtain(definition)
            positiveFlow = positiveFlow.width(installationStep)
        }

        var negativelow = InstallationStepFlow(toolkit)
        definitions = getDefinitions(false, false)
        definitions.forEach { definition ->
            val installationStep = factory.obtain(definition)
            negativelow = negativelow.width(installationStep)
        }

        negativelow
                .registerRecipe(
                        CommandInstallationStep::class,
                        CommandInstallationStepRecipe::class
                )
                .registerRecipe(
                        Condition::class,
                        ConditionRecipe::class
                )
                .onFinish(flowCallback)

        positiveFlow
                .registerRecipe(
                        CommandInstallationStep::class,
                        CommandInstallationStepRecipe::class
                )
                .registerRecipe(
                        Condition::class,
                        ConditionRecipe::class
                )
                .onFinish(flowCallback)
                .connect(negativelow)
                .run()

        while (finished < 1 || failed < 1) {
            Thread.yield()
        }

        Assertions.assertEquals(1, finished)
        Assertions.assertEquals(1, failed)
        log.i("Condition step flow test completed")
    }

    private fun getDefinitions(fails: Boolean, alreadyInstalled: Boolean): List<InstallationStepDefinition> {
        return if (fails) {
            listOf(
                    InstallationStepDefinition(
                            type = InstallationStepType.CONDITION.type,
                            value = "This one will fail"
                    ),
                    InstallationStepDefinition(
                            type = InstallationStepType.COMMAND.type,
                            value = "echo 'This one will be executed'"
                    )
            )
        } else {
            if (alreadyInstalled) {
                listOf(
                        InstallationStepDefinition(
                                type = InstallationStepType.CONDITION.type,
                                value = "echo 'Condition'"
                        ),
                        InstallationStepDefinition(
                                type = InstallationStepType.COMMAND.type,
                                value = "echo 'This one will not be executed'"
                        )
                )
            } else {
                listOf(
                        InstallationStepDefinition(
                                type = InstallationStepType.CONDITION.type,
                                value = "which does_not_exist"
                        ),
                        InstallationStepDefinition(
                                type = InstallationStepType.COMMAND.type,
                                value = "echo 'This one will be executed'"
                        )
                )
            }
        }
    }
}