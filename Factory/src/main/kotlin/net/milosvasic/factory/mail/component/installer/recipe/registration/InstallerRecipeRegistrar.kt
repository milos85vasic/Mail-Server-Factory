package net.milosvasic.factory.mail.component.installer.recipe.registration

import net.milosvasic.factory.mail.component.installer.recipe.PackageManagerInstallationStepRecipe
import net.milosvasic.factory.mail.component.installer.step.InstallationStep
import net.milosvasic.factory.mail.component.installer.step.PackageManagerInstallationStep
import net.milosvasic.factory.mail.execution.flow.implementation.InstallationStepFlow
import net.milosvasic.factory.mail.execution.flow.processing.ProcessingRecipesRegistration

class InstallerRecipeRegistrar : ProcessingRecipesRegistration {

    override fun registerRecipes(step: InstallationStep<*>, flow: InstallationStepFlow): Boolean {
        when (step::class) {
            PackageManagerInstallationStep::class -> {
                flow.registerRecipe(
                        PackageManagerInstallationStep::class,
                        PackageManagerInstallationStepRecipe::class
                )
                return true
            }
        }
        return false
    }
}