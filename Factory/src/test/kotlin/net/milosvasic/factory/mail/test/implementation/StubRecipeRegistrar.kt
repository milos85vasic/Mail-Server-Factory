package net.milosvasic.factory.mail.test.implementation

import net.milosvasic.factory.mail.component.docker.recipe.StackRecipe
import net.milosvasic.factory.mail.component.installer.recipe.DeployRecipe
import net.milosvasic.factory.mail.component.installer.step.InstallationStep
import net.milosvasic.factory.mail.execution.flow.implementation.InstallationStepFlow
import net.milosvasic.factory.mail.execution.flow.processing.ProcessingRecipesRegistration

class StubRecipeRegistrar : ProcessingRecipesRegistration {

    override fun registerRecipes(step: InstallationStep<*>, flow: InstallationStepFlow): Boolean {
        when (step::class) {
            StubDeploy::class -> {
                flow.registerRecipe(
                        StubDeploy::class,
                        DeployRecipe::class
                )
                return true
            }
            StubStack::class -> {
                flow.registerRecipe(
                        StubStack::class,
                        StackRecipe::class
                )
                return true
            }
        }
        return false
    }
}