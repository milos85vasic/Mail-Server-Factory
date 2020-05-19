package net.milosvasic.factory.mail.test

import net.milosvasic.factory.mail.component.Toolkit
import net.milosvasic.factory.mail.component.installer.recipe.CommandInstallationStepRecipe
import net.milosvasic.factory.mail.component.installer.recipe.ConditionRecipe
import net.milosvasic.factory.mail.component.installer.recipe.DeployRecipe
import net.milosvasic.factory.mail.component.installer.step.CommandInstallationStep
import net.milosvasic.factory.mail.component.installer.step.InstallationStepFactory
import net.milosvasic.factory.mail.component.installer.step.InstallationStepType
import net.milosvasic.factory.mail.component.installer.step.condition.Condition
import net.milosvasic.factory.mail.component.installer.step.condition.SkipCondition
import net.milosvasic.factory.mail.configuration.InstallationStepDefinition
import net.milosvasic.factory.mail.execution.flow.callback.FlowCallback
import net.milosvasic.factory.mail.execution.flow.implementation.InstallationStepFlow
import net.milosvasic.factory.mail.log
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.operation.OperationResultListener
import net.milosvasic.factory.mail.terminal.Commands
import net.milosvasic.factory.mail.terminal.TerminalCommand
import net.milosvasic.factory.mail.test.implementation.StubConnection
import net.milosvasic.factory.mail.test.implementation.StubDeploy
import net.milosvasic.factory.mail.test.implementation.StubSSH
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class DeployStepTest : BaseTest() {

    private val destination = "build/Mocks/Deploy"
    private val factory = InstallationStepFactory()
    private val protos = listOf("proto.stub.txt")
    private val mocks = listOf("Anthem.txt", "stub.txt")

    @Test
    fun testDeployStep() {
        initLogging()
        log.i("Deploy step flow test started")

        var finished = 0
        var commandsFailed = 0
        var commandsExecuted = 0

        val ssh = StubSSH()
        val terminal = StubConnection()
        val remoteToolkit = Toolkit(ssh)
        val localToolkit = Toolkit(terminal)
        val init = InstallationStepFlow(localToolkit)

        val commandCallback = object : OperationResultListener {
            override fun onOperationPerformed(result: OperationResult) {

                when (result.operation) {
                    is TerminalCommand -> {
                        log.v("Executed: $result")
                        if (result.success) {
                            commandsExecuted++
                        } else {
                            commandsFailed++
                        }
                    }
                }
            }
        }

        val flowCallback = object : FlowCallback<String> {
            override fun onFinish(success: Boolean, message: String, data: String?) {

                if (!success) {
                    log.w(message)
                }
                finished++
            }
        }

        val initFlowCallback = object : FlowCallback<String> {
            override fun onFinish(success: Boolean, message: String, data: String?) {

                flowCallback.onFinish(success, message, data)
                terminal.subscribe(commandCallback)
            }
        }

        registerRecipes(init).onFinish(initFlowCallback)
        fun getPath(mock: String) = "$destination/$mock"
        mocks.forEach { mock ->
            val path = getPath(mock)
            val command = Commands.test(path)
            init.width(conditionStep(command))
        }
        mocks.forEach { mock ->
            val path = getPath(mock)
            init.width(commandStep(Commands.rm(path)))
        }

        val flow = InstallationStepFlow(remoteToolkit)
        registerRecipes(flow)
                .width(deployStep())
                .onFinish(flowCallback)

        val verification = InstallationStepFlow(localToolkit)
        registerRecipes(verification).onFinish(flowCallback)

        protos.forEach {
            val path = getPath(it)
            val command = Commands.test(path)
            verification.width(skipConditionStep(command))
        }
        mocks.forEach { mock ->
            val path = getPath(mock)
            val command = Commands.test(path)
            verification.width(conditionStep(command))
        }

        init
                .connect(flow)
                .connect(verification)
                .run()

        while (init.isBusy()) {
            Thread.yield()
        }

        terminal.unsubscribe(commandCallback)
        Assertions.assertEquals(3, finished)
        log.v("Commands executed: $commandsExecuted")
        log.v("Commands failed: $commandsFailed")
        Assertions.assertEquals(2, commandsExecuted)
        Assertions.assertEquals(1, commandsFailed)

        log.i("Deploy step flow test completed")
    }

    private fun registerRecipes(flow: InstallationStepFlow) =
            flow
                    .registerRecipe(
                            CommandInstallationStep::class,
                            CommandInstallationStepRecipe::class
                    )
                    .registerRecipe(
                            SkipCondition::class,
                            ConditionRecipe::class
                    )
                    .registerRecipe(
                            Condition::class,
                            ConditionRecipe::class
                    )
                    .registerRecipe(
                            StubDeploy::class,
                            DeployRecipe::class
                    )

    private fun conditionStep(command: String) =
            factory.obtain(
                    InstallationStepDefinition(
                            type = InstallationStepType.CONDITION.type,
                            value = command
                    )
            )

    private fun skipConditionStep(command: String) =
            factory.obtain(
                    InstallationStepDefinition(
                            type = InstallationStepType.SKIP_CONDITION.type,
                            value = command
                    )
            )

    private fun commandStep(command: String) =
            factory.obtain(
                    InstallationStepDefinition(
                            type = InstallationStepType.COMMAND.type,
                            value = command
                    )
            )

    private fun deployStep() = StubDeploy("Mocks/Deploy", destination, protos)
}