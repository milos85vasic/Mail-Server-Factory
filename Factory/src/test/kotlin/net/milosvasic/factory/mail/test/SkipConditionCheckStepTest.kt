package net.milosvasic.factory.mail.test

import net.milosvasic.factory.mail.component.Toolkit
import net.milosvasic.factory.mail.component.installer.recipe.CommandInstallationStepRecipe
import net.milosvasic.factory.mail.component.installer.recipe.ConditionRecipe
import net.milosvasic.factory.mail.component.installer.step.CommandInstallationStep
import net.milosvasic.factory.mail.component.installer.step.InstallationStepFactory
import net.milosvasic.factory.mail.component.installer.step.InstallationStepType
import net.milosvasic.factory.mail.configuration.InstallationStepDefinition
import net.milosvasic.factory.mail.execution.flow.callback.FlowCallback
import net.milosvasic.factory.mail.execution.flow.implementation.InstallationStepFlow
import net.milosvasic.factory.mail.fail
import net.milosvasic.factory.mail.log
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.operation.OperationResultListener
import net.milosvasic.factory.mail.test.implementation.StubCheck
import net.milosvasic.factory.mail.test.implementation.StubConnection
import net.milosvasic.factory.mail.test.implementation.StubSkipConditionCheck
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class SkipConditionCheckStepTest : BaseTest() {

    private val iterations = 5
    private val connection = StubConnection()
    private val toolkit = Toolkit(connection)
    private val factory = InstallationStepFactory()
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
                assert(result.success)
                executed++
            }
        }

        connection.terminal.subscribe(operationResultListener)

        val flowCallback = object : FlowCallback<String> {

            override fun onFinish(success: Boolean, message: String, data: String?) {
                if (success) {
                    finished++
                } else {
                    log.e(message)
                    failed++
                }
            }
        }

        positiveFlow.width(StubSkipConditionCheck(true))
        appendCommands(positiveFlow)
        positiveFlow
                .registerRecipe(CommandInstallationStep::class, CommandInstallationStepRecipe::class)
                .registerRecipe(StubSkipConditionCheck::class, ConditionRecipe::class)
                .onFinish(flowCallback)

        negativeFlow.width(StubSkipConditionCheck(false))
        appendCommands(negativeFlow)
        negativeFlow
                .registerRecipe(CommandInstallationStep::class, CommandInstallationStepRecipe::class)
                .registerRecipe(StubSkipConditionCheck::class, ConditionRecipe::class)
                .onFinish(flowCallback)

        mainFlow
                .connect(positiveFlow)
                .connect(negativeFlow)
                .onFinish(flowCallback)
                .run()

        while (mainFlow.isBusy()) {
            Thread.yield()
        }

        connection.terminal.unsubscribe(operationResultListener)
        Assertions.assertEquals(2, finished)
        Assertions.assertEquals(1, failed)
        Assertions.assertEquals(iterations + 1, executed)
        log.i("Skip condition check step test completed")
    }

    private fun appendCommands(flow: InstallationStepFlow) {

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