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
import org.junit.jupiter.api.Test

class ConditionStepFlowTest : BaseTest() {

    @Test
    fun testConditionStepFlow() {
        initLogging()
        log.i("Condition step flow test started")

        var finished = false
        val connection = StubConnection()
        val toolkit = Toolkit(connection)
        val factory = InstallationStepFactory()
        var flow = InstallationStepFlow(toolkit)

        val flowCallback = object : FlowCallback<String> {

            override fun onFinish(success: Boolean, message: String, data: String?) {
                if (!success) {
                    log.e(message)
                }
                assert(success)
                finished = true
            }
        }

        val definitions = listOf(
                InstallationStepDefinition(
                        type = InstallationStepType.CONDITION.type,
                        value = "echo 'Condition'"
                ),
                InstallationStepDefinition(
                        type = InstallationStepType.COMMAND.type,
                        value = "echo 'This one will not be executed'"
                )
        )

        definitions.forEach { definition ->
            val installationStep = factory.obtain(definition)
            flow = flow.width(installationStep)
        }

        flow
                .registerRecipe(
                        CommandInstallationStep::class,
                        CommandInstallationStepRecipe::class
                )
                .registerRecipe(
                        Condition::class,
                        ConditionRecipe::class
                )
                .onFinish(flowCallback)
                .run()

        while (!finished) {
            Thread.yield()
        }

        log.i("Condition step flow test completed")
    }
}