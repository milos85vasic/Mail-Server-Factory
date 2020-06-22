package net.milosvasic.factory.test

import net.milosvasic.factory.common.Registration
import net.milosvasic.factory.component.Toolkit
import net.milosvasic.factory.component.installer.recipe.CommandInstallationStepRecipe
import net.milosvasic.factory.component.installer.recipe.ConditionRecipe
import net.milosvasic.factory.component.installer.recipe.DeployRecipe
import net.milosvasic.factory.component.installer.step.CommandInstallationStep
import net.milosvasic.factory.component.installer.step.InstallationStepType
import net.milosvasic.factory.component.installer.step.condition.Condition
import net.milosvasic.factory.component.installer.step.condition.SkipCondition
import net.milosvasic.factory.component.installer.step.factory.InstallationStepFactories
import net.milosvasic.factory.configuration.InstallationStepDefinition
import net.milosvasic.factory.execution.flow.callback.FlowCallback
import net.milosvasic.factory.execution.flow.implementation.InstallationStepFlow
import net.milosvasic.factory.execution.flow.implementation.RegistrationFlow
import net.milosvasic.factory.log
import net.milosvasic.factory.operation.OperationResult
import net.milosvasic.factory.operation.OperationResultListener
import net.milosvasic.factory.terminal.TerminalCommand
import net.milosvasic.factory.terminal.command.Commands
import net.milosvasic.factory.test.implementation.StubConnection
import net.milosvasic.factory.test.implementation.StubDeploy
import net.milosvasic.factory.test.implementation.StubSSH
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class DeployStepTest : BaseTest() {

    private val archive = "Deploy.tar.gz"
    private val mocksDirectory = "Mocks"
    private val deployDirectory = "$mocksDirectory/Deploy"
    private val destination = "build/$deployDirectory"
    private val factory = InstallationStepFactories
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

        val flowCallback = object : FlowCallback {
            override fun onFinish(success: Boolean) {
                finished++
            }
        }

        val initFlowCallback = object : FlowCallback {
            override fun onFinish(success: Boolean) {
                flowCallback.onFinish(success)
            }
        }

        val registration = object : Registration<OperationResultListener> {
            override fun register(what: OperationResultListener) {
                terminal.subscribe(what)
            }

            override fun unRegister(what: OperationResultListener) {
                terminal.unsubscribe(what)
            }
        }

        val register = RegistrationFlow<OperationResultListener>()
                .width(registration)
                .perform(commandCallback)

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

        var cmd = Commands.test(getPath(archive))
        verification.width(skipConditionStep(cmd))
        cmd = Commands.test("$mocksDirectory/$archive")
        verification.width(skipConditionStep(cmd))
        mocks.forEach { mock ->
            val path = getPath(mock)
            val command = Commands.test(path)
            verification.width(conditionStep(command))
        }

        init
                .connect(flow)
                .connect(register)
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
        Assertions.assertEquals(3, commandsFailed)

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