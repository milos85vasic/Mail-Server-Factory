package net.milosvasic.factory.mail.test

import net.milosvasic.factory.mail.EMPTY
import net.milosvasic.factory.mail.common.DataHandler
import net.milosvasic.factory.mail.component.Toolkit
import net.milosvasic.factory.mail.component.docker.step.stack.Check
import net.milosvasic.factory.mail.component.installer.recipe.CommandInstallationStepRecipe
import net.milosvasic.factory.mail.component.installer.recipe.ConditionRecipe
import net.milosvasic.factory.mail.component.installer.step.CommandInstallationStep
import net.milosvasic.factory.mail.component.installer.step.InstallationStepFactory
import net.milosvasic.factory.mail.component.installer.step.InstallationStepType
import net.milosvasic.factory.mail.configuration.InstallationStepDefinition
import net.milosvasic.factory.mail.execution.flow.callback.FlowCallback
import net.milosvasic.factory.mail.execution.flow.implementation.InstallationStepFlow
import net.milosvasic.factory.mail.log
import net.milosvasic.factory.mail.test.implementation.StubConnection
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class CheckStepTest : BaseTest() {

    private val iterations = 5
    private val factory = InstallationStepFactory()

    @Test
    fun testCheckStep() {
        initLogging()
        log.i("Check step test started")

        var finished = false
        val connection = StubConnection()
        val toolkit = Toolkit(connection)
        var flow = InstallationStepFlow(toolkit)

        val dataHandler = object : DataHandler<String> {
            override fun onData(data: String?) {
                log.v("Data: $data")
                Assertions.assertNotNull(data)
                assert(data != String.EMPTY)

            }
        }

        val flowCallback = object : FlowCallback<String> {

            override fun onFinish(success: Boolean, message: String, data: String?) {
                if (!success) {
                    log.e(message)
                }
                assert(success)
                finished = true
            }
        }

        flow = appendCommands(flow)
        flow = appendCheck(flow)
        flow = appendCommands(flow)
        flow
                .registerRecipe(CommandInstallationStep::class, CommandInstallationStepRecipe::class)
                .registerRecipe(Check::class, ConditionRecipe::class)
                .onFinish(flowCallback)
                .run()

        while (flow.isBusy()) {
            Thread.yield()
        }

        assert(finished)
        log.i("Check step test completed")
    }

    private fun appendCheck(flow: InstallationStepFlow): InstallationStepFlow {

        var flow1 = flow
        val definition = InstallationStepDefinition(
                type = InstallationStepType.CHECK.type,
                value = "echo 'Test'"
        )
        val installationStep = factory.obtain(definition)
        flow1 = flow1.width(installationStep)
        return flow1
    }

    private fun appendCommands(flow: InstallationStepFlow): InstallationStepFlow {

        var flow1 = flow
        for (x in 0 until iterations) {
            val definition = InstallationStepDefinition(
                    type = InstallationStepType.COMMAND.type,
                    value = "echo 'Test: $x'"
            )
            val installationStep = factory.obtain(definition)
            flow1 = flow1.width(installationStep)
        }
        return flow1
    }
}