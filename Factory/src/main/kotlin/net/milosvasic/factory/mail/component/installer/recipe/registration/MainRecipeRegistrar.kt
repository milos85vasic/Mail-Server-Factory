package net.milosvasic.factory.mail.component.installer.recipe.registration

import net.milosvasic.factory.mail.component.installer.recipe.CommandInstallationStepRecipe
import net.milosvasic.factory.mail.component.installer.recipe.ConditionRecipe
import net.milosvasic.factory.mail.component.installer.recipe.DeployRecipe
import net.milosvasic.factory.mail.component.installer.recipe.RebootRecipe
import net.milosvasic.factory.mail.component.installer.step.CommandInstallationStep
import net.milosvasic.factory.mail.component.installer.step.InstallationStep
import net.milosvasic.factory.mail.component.installer.step.condition.Condition
import net.milosvasic.factory.mail.component.installer.step.condition.SkipCondition
import net.milosvasic.factory.mail.component.installer.step.deploy.Deploy
import net.milosvasic.factory.mail.component.installer.step.reboot.Reboot
import net.milosvasic.factory.mail.execution.flow.implementation.InstallationStepFlow
import net.milosvasic.factory.mail.execution.flow.processing.ProcessingRecipesRegistration

class MainRecipeRegistrar : ProcessingRecipesRegistration {

    override fun registerRecipes(step: InstallationStep<*>, flow: InstallationStepFlow): Boolean {
        when (step) {
            is CommandInstallationStep -> {
                flow.registerRecipe(
                        CommandInstallationStep::class,
                        CommandInstallationStepRecipe::class
                )
                return true
            }
            is Condition -> {
                flow.registerRecipe(
                        Condition::class,
                        ConditionRecipe::class
                )
                return true
            }
            is SkipCondition -> {
                flow.registerRecipe(
                        SkipCondition::class,
                        ConditionRecipe::class
                )
                return true
            }
            is Deploy -> {
                flow.registerRecipe(
                        Deploy::class,
                        DeployRecipe::class
                )
                return true
            }
            is Reboot -> {
                flow.registerRecipe(
                        Reboot::class,
                        RebootRecipe::class
                )
                return true
            }
        }
        return false
    }
}