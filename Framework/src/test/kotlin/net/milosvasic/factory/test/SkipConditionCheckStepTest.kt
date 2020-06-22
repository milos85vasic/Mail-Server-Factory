package net.milosvasic.factory.test

import net.milosvasic.factory.component.Toolkit
import net.milosvasic.factory.component.installer.recipe.CommandInstallationStepRecipe
import net.milosvasic.factory.component.installer.recipe.ConditionRecipe
import net.milosvasic.factory.component.installer.step.CommandInstallationStep
import net.milosvasic.factory.component.installer.step.InstallationStepType
import net.milosvasic.factory.component.installer.step.factory.InstallationStepFactories
import net.milosvasic.factory.configuration.InstallationStepDefinition
import net.milosvasic.factory.execution.flow.callback.FlowCallback
import net.milosvasic.factory.execution.flow.implementation.InstallationStepFlow
import net.milosvasic.factory.log
import net.milosvasic.factory.operation.OperationResult
import net.milosvasic.factory.operation.OperationResultListener
import net.milosvasic.factory.test.implementation.StubConnection
import net.milosvasic.factory.test.implementation.StubSkipConditionCheck
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class SkipConditionCheckStepTest : BaseTest() {

    private val iterations = 5
    private val connection = StubConnection()
    private val toolkit = Toolkit(connection)
    private val factory = InstallationStepFactories
    private val mainFlow = InstallationStepFlow(toolkit)
    private val positiveFlow = InstallationStepFlow(toolkit)
    private val negativeFlow = InstallationStepFlow(toolkit)

    @Test
    fun testSkipConditionCheck() {
        initLogging()
        log.i("Skip condition check step test started")

        var failed = 0
        var finished = 0
        var executed = 0

        val operationResultListener = object : OperationResultListener {
            override fun onOperationPerformed(result: OperationResult) {
                executed++
            }
        }

        connection.getTerminal().subscribe(operationResultListener)

        val flowCallback = object : FlowCallback {

            override fun onFinish(success: Boolean) {
                if (success) {
                    finished++
                } else {
                    failed++
                }
            }
        }

        positiveFlow.width(StubSkipConditionCheck(false))
        appendCommands("Positive", positiveFlow)
        positiveFlow
                .registerRecipe(CommandInstallationStep::class, CommandInstallationStepRecipe::class)
                .registerRecipe(StubSkipConditionCheck::class, ConditionRecipe::class)
                .onFinish(flowCallback)

        negativeFlow.width(StubSkipConditionCheck(true))
        appendCommands("Negative", negativeFlow)
        negativeFlow
                .registerRecipe(CommandInstallationStep::class, CommandInstallationStepRecipe::class)
                .registerRecipe(StubSkipConditionCheck::class, ConditionRecipe::class)
                .onFinish(flowCallback)

        mainFlow
                .registerRecipe(CommandInstallationStep::class, CommandInstallationStepRecipe::class)
                .connect(positiveFlow)
                .connect(negativeFlow)
                .onFinish(flowCallback)

        appendCommands("Main", mainFlow)
        mainFlow.run()

        while (mainFlow.isBusy()) {
            Thread.yield()
        }

        connection.getTerminal().unsubscribe(operationResultListener)
        Assertions.assertEquals(3, finished)
        Assertions.assertEquals(0, failed)
        Assertions.assertEquals((iterations * 2) + 2, executed)
        log.i("Skip condition check step test completed")
    }

    private fun appendCommands(prefix: String, flow: InstallationStepFlow) {

        for (x in 0 until iterations) {
            val definition = InstallationStepDefinition(
                    type = InstallationStepType.COMMAND.type,
                    value = "echo 'Test :: $prefix :: $x'"
            )
            val installationStep = factory.obtain(definition)
            flow.width(installationStep)
        }
    }
}