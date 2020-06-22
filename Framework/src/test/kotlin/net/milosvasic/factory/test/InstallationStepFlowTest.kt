package net.milosvasic.factory.test

import net.milosvasic.factory.component.Toolkit
import net.milosvasic.factory.component.installer.recipe.CommandInstallationStepRecipe
import net.milosvasic.factory.component.installer.step.CommandInstallationStep
import net.milosvasic.factory.component.installer.step.InstallationStepType
import net.milosvasic.factory.component.installer.step.factory.InstallationStepFactories
import net.milosvasic.factory.configuration.InstallationStepDefinition
import net.milosvasic.factory.execution.flow.callback.FlowCallback
import net.milosvasic.factory.execution.flow.implementation.InstallationStepFlow
import net.milosvasic.factory.log
import net.milosvasic.factory.test.implementation.StubConnection
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

        val flowCallback = object : FlowCallback {

            override fun onFinish(success: Boolean) {
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