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
import net.milosvasic.factory.operation.OperationResult
import net.milosvasic.factory.operation.OperationResultListener
import net.milosvasic.factory.test.implementation.StubCheck
import net.milosvasic.factory.test.implementation.StubConnection
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class CheckStepTest : BaseTest() {

    private val iterations = 5
    private val connection = StubConnection()
    private val toolkit = Toolkit(connection)
    private val factory = InstallationStepFactories
    private val flow = InstallationStepFlow(toolkit)

    @Test
    fun testCheckStep() {
        initLogging()
        log.i("Check step test started")

        var executed = 0
        var finished = false

        val operationResultListener = object : OperationResultListener {
            override fun onOperationPerformed(result: OperationResult) {
                assert(result.success)
                executed++
            }
        }

        connection.getTerminal().subscribe(operationResultListener)

        val flowCallback = object : FlowCallback {

            override fun onFinish(success: Boolean) {
                assert(success)
                finished = true
            }
        }

        appendCommands()
        flow.width(StubCheck())
        appendCommands()
        flow
                .registerRecipe(CommandInstallationStep::class, CommandInstallationStepRecipe::class)
                .registerRecipe(StubCheck::class, CommandInstallationStepRecipe::class)
                .onFinish(flowCallback)
                .run()

        while (flow.isBusy()) {
            Thread.yield()
        }

        connection.getTerminal().unsubscribe(operationResultListener)
        assert(finished)
        Assertions.assertEquals((iterations * 2) + 1, executed)
        log.i("Check step test completed")
    }

    private fun appendCommands() {

        for (x in 0 until iterations) {
            val definition = InstallationStepDefinition(
                    type = InstallationStepType.COMMAND.type,
                    value = "echo 'Test: $x'"
            )
            val installationStep = factory.obtain(definition)
            flow.width(installationStep)
        }
    }
}