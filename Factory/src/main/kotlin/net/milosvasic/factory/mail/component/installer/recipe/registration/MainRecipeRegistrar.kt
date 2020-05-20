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
        when (step::class) {
            CommandInstallationStep::class -> {
                flow.registerRecipe(
                        CommandInstallationStep::class,
                        CommandInstallationStepRecipe::class
                )
                return true
            }
            Condition::class -> {
                flow.registerRecipe(
                        Condition::class,
                        ConditionRecipe::class
                )
                return true
            }
            SkipCondition::class -> {
                flow.registerRecipe(
                        SkipCondition::class,
                        ConditionRecipe::class
                )
                return true
            }
            Deploy::class -> {
                flow.registerRecipe(
                        Deploy::class,
                        DeployRecipe::class
                )
                return true
            }
            Reboot::class -> {
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