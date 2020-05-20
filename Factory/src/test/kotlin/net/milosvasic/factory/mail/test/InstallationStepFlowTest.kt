package net.milosvasic.factory.mail.test

import net.milosvasic.factory.mail.component.Toolkit
import net.milosvasic.factory.mail.component.installer.recipe.CommandInstallationStepRecipe
import net.milosvasic.factory.mail.component.installer.step.CommandInstallationStep
import net.milosvasic.factory.mail.component.installer.step.InstallationStepType
import net.milosvasic.factory.mail.component.installer.step.factory.InstallationStepFactories
import net.milosvasic.factory.mail.configuration.InstallationStepDefinition
import net.milosvasic.factory.mail.execution.flow.callback.FlowCallback
import net.milosvasic.factory.mail.execution.flow.implementation.InstallationStepFlow
import net.milosvasic.factory.mail.log
import net.milosvasic.factory.mail.test.implementation.StubConnection
import org.junit.jupiter.api.Test

class InstallationStepFlowTest : BaseTest() {

    @Test
    fun testInstallationStepFlow() {
        initLogging()
        log.i("Installation step flow test started")

        val iterations = 5
        var finished = false
        val connection = StubConnection()
        val toolkit = Toolkit(connection)
        val factory = InstallationStepFactories
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

        for (x in 0 until iterations) {
            val definition = InstallationStepDefinition(
                    type = InstallationStepType.COMMAND.type,
                    value = "echo 'Test: $x'"
            )
            val installationStep = factory.obtain(definition)
            flow = flow.width(installationStep)
        }
        flow
                .registerRecipe(CommandInstallationStep::class, CommandInstallationStepRecipe::class)
                .onFinish(flowCallback)
                .run()

        while (flow.isBusy()) {
            Thread.yield()
        }

        assert(finished)
        log.i("Installation step flow test completed")
    }
}