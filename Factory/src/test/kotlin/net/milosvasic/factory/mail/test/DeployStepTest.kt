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
import net.milosvasic.factory.mail.component.installer.step.deploy.Deploy
import net.milosvasic.factory.mail.configuration.InstallationStepDefinition
import net.milosvasic.factory.mail.execution.flow.callback.FlowCallback
import net.milosvasic.factory.mail.execution.flow.implementation.InstallationStepFlow
import net.milosvasic.factory.mail.log
import net.milosvasic.factory.mail.terminal.Commands
import net.milosvasic.factory.mail.test.implementation.StubDeploy
import net.milosvasic.factory.mail.test.implementation.StubSSH
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.File

class DeployStepTest : BaseTest() {

    private val factory = InstallationStepFactory()
    private val mocks = listOf("Anthem.txt", "proto.stub.txt")

    @Test
    fun testDeployStep() {
        initLogging()
        log.i("Deploy step flow test started")

        var finished = 0
        val flowCallback = object : FlowCallback<String> {

            override fun onFinish(success: Boolean, message: String, data: String?) {

                if (!success) {
                    log.w(message)
                }
                finished++
            }
        }

        val connection = StubSSH()
        val toolkit = Toolkit(connection)
        val init = InstallationStepFlow(toolkit)

        registerRecipes(init).onFinish(flowCallback)
        fun getPath(mock: String) = "build${File.separator}$mock"
        mocks.forEach { mock ->
            val path = getPath(mock)
            val command = Commands.test(path)
            init.width(conditionStep(command))
        }
        mocks.forEach { mock ->
            val path = getPath(mock)
            init.width(commandStep(Commands.rm(path)))
        }

        val flow = InstallationStepFlow(toolkit)
        registerRecipes(flow)
                .width(deployStep())
                .onFinish(flowCallback)

        init
                .connect(flow)
                .run()

        while (init.isBusy()) {
            Thread.yield()
        }

        Assertions.assertEquals(2, finished)

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

    private fun deployStep() = StubDeploy("Mocks/Deploy", "build/Mocks/Deploy")
}