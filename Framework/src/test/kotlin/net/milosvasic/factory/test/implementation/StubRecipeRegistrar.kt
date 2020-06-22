package net.milosvasic.factory.test.implementation

import net.milosvasic.factory.component.docker.recipe.StackRecipe
import net.milosvasic.factory.component.installer.recipe.DeployRecipe
import net.milosvasic.factory.component.installer.step.InstallationStep
import net.milosvasic.factory.execution.flow.implementation.InstallationStepFlow
import net.milosvasic.factory.execution.flow.processing.ProcessingRecipesRegistration

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