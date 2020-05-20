package net.milosvasic.factory.mail.component.docker.recipe

import net.milosvasic.factory.mail.component.docker.step.stack.Check
import net.milosvasic.factory.mail.component.docker.step.stack.SkipConditionCheck
import net.milosvasic.factory.mail.component.docker.step.stack.Stack
import net.milosvasic.factory.mail.component.installer.recipe.CommandInstallationStepRecipe
import net.milosvasic.factory.mail.component.installer.recipe.ConditionRecipe
import net.milosvasic.factory.mail.component.installer.step.InstallationStep
import net.milosvasic.factory.mail.execution.flow.implementation.InstallationStepFlow
import net.milosvasic.factory.mail.execution.flow.processing.ProcessingRecipesRegistration

class DockerRecipeRegistrar : ProcessingRecipesRegistration {

    override fun registerRecipes(step: InstallationStep<*>, flow: InstallationStepFlow) : Boolean {
        when (step::class) {
            Stack::class -> {
                flow.registerRecipe(
                        Stack::class,
                        StackRecipe::class
                )
                return true
            }
            Check::class -> {
                flow.registerRecipe(
                        Check::class,
                        CommandInstallationStepRecipe::class
                )
                return true
            }
            SkipConditionCheck::class -> {
                flow.registerRecipe(
                        SkipConditionCheck::class,
                        ConditionRecipe::class
                )
                return true
            }
        }
        return false
    }
}