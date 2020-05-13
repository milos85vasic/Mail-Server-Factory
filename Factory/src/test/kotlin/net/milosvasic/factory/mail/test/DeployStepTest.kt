package net.milosvasic.factory.mail.test

import net.milosvasic.factory.mail.component.installer.recipe.CommandInstallationStepRecipe
import net.milosvasic.factory.mail.component.installer.recipe.ConditionRecipe
import net.milosvasic.factory.mail.component.installer.recipe.DeployRecipe
import net.milosvasic.factory.mail.component.installer.step.CommandInstallationStep
import net.milosvasic.factory.mail.component.installer.step.condition.Condition
import net.milosvasic.factory.mail.component.installer.step.deploy.Deploy
import net.milosvasic.factory.mail.execution.flow.implementation.InstallationStepFlow
import net.milosvasic.factory.mail.log
import org.junit.jupiter.api.Test

class DeployStepTest : BaseTest() {

    @Test
    fun testDeployStep() {
        initLogging()
        log.i("Deploy step flow test started")



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
}