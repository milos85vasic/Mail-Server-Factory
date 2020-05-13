package net.milosvasic.factory.mail.test

import net.milosvasic.factory.mail.EMPTY
import net.milosvasic.factory.mail.component.Toolkit
import net.milosvasic.factory.mail.component.installer.recipe.CommandInstallationStepRecipe
import net.milosvasic.factory.mail.component.installer.recipe.ConditionRecipe
import net.milosvasic.factory.mail.component.installer.recipe.DeployRecipe
import net.milosvasic.factory.mail.component.installer.step.CommandInstallationStep
import net.milosvasic.factory.mail.component.installer.step.InstallationStepFactory
import net.milosvasic.factory.mail.component.installer.step.InstallationStepType
import net.milosvasic.factory.mail.component.installer.step.condition.Condition
import net.milosvasic.factory.mail.component.installer.step.deploy.Deploy
import net.milosvasic.factory.mail.configuration.InstallationStepDefinition
import net.milosvasic.factory.mail.execution.flow.callback.FlowCallback
import net.milosvasic.factory.mail.execution.flow.implementation.CommandFlow
import net.milosvasic.factory.mail.execution.flow.implementation.InstallationStepFlow
import net.milosvasic.factory.mail.log
import net.milosvasic.factory.mail.terminal.Commands
import net.milosvasic.factory.mail.test.implementation.StubSSH
import org.junit.jupiter.api.Test

class DeployStepTest : BaseTest() {

    private val factory = InstallationStepFactory()
    private val mocks = listOf("Anthem.txt", "proto.stub.txt")

    @Test
    fun testDeployStep() {
        initLogging()
        log.i("Deploy step flow test started")

        var finished = false
        val flowCallback = object : FlowCallback<String> {

            override fun onFinish(success: Boolean, message: String, data: String?) {

                if (!success) {
                    log.w(message)
                }
                finished = true
            }
        }

        val connection = StubSSH()
        val toolkit = Toolkit(connection)
        val init = InstallationStepFlow(toolkit)

        registerRecipes(init)
                .width(conditionStep(mocksDeployed()))
                .width(commandStep(mocksCleanup()))
                .onFinish(flowCallback)

                .run()

        while (!finished) {
            Thread.yield()
        }

        log.i("Deploy step flow test completed")
    }

    private fun registerRecipes(flow: InstallationStepFlow) =
            flow
                    .registerRecipe(
                            CommandInstallationStep::class,
                            CommandInstallationStepRecipe::class
                    )
                    .registerRecipe(
                            Condition::class,
                            ConditionRecipe::class
                    )
                    .registerRecipe(
                            Deploy::class,
                            DeployRecipe::class
                    )

    private fun conditionStep(command: String) =
            factory.obtain(
                    InstallationStepDefinition(
                            type = InstallationStepType.CONDITION.type,
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

    private fun mocksDeployed(): String {
        var command = ""
        mocks.forEachIndexed { index, mock ->
            command += if (index == 0) {
                "file build/$mock"
            } else {
                " build/$mock"
            }
        }
        assert(command != String.EMPTY)
        return command
    }

    private fun mocksCleanup(): String {
        var command = ""
        mocks.forEachIndexed { index, mock ->
            command += if (index == 0) {
                Commands.rm("build/$mock")
            } else {
                " && ${Commands.rm("build/$mock")}"
            }
        }
        assert(command != String.EMPTY)
        return command
    }
}