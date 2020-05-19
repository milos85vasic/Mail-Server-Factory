package net.milosvasic.factory.mail.test

import net.milosvasic.factory.mail.component.Toolkit
import net.milosvasic.factory.mail.component.installer.recipe.CommandInstallationStepRecipe
import net.milosvasic.factory.mail.component.installer.step.CommandInstallationStep
import net.milosvasic.factory.mail.component.installer.step.InstallationStepFactory
import net.milosvasic.factory.mail.component.installer.step.InstallationStepType
import net.milosvasic.factory.mail.configuration.InstallationStepDefinition
import net.milosvasic.factory.mail.execution.flow.callback.FlowCallback
import net.milosvasic.factory.mail.execution.flow.implementation.InstallationStepFlow
import net.milosvasic.factory.mail.log
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.operation.OperationResultListener
import net.milosvasic.factory.mail.test.implementation.StubCheck
import net.milosvasic.factory.mail.test.implementation.StubConnection
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class CheckStepTest : BaseTest() {

    private val iterations = 5
    private val connection = StubConnection()
    private val toolkit = Toolkit(connection)
    private val factory = InstallationStepFactory()
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

        val flowCallback = object : FlowCallback<String> {

            override fun onFinish(success: Boolean, message: String, data: String?) {
                if (!success) {
                    log.e(message)
                }
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