package net.milosvasic.factory.component.installer.recipe.registration

import net.milosvasic.factory.component.installer.recipe.*
import net.milosvasic.factory.component.installer.step.CommandInstallationStep
import net.milosvasic.factory.component.installer.step.InstallationStep
import net.milosvasic.factory.component.installer.step.certificate.Certificate
import net.milosvasic.factory.component.installer.step.certificate.TlsCertificate
import net.milosvasic.factory.component.installer.step.condition.Condition
import net.milosvasic.factory.component.installer.step.condition.SkipCondition
import net.milosvasic.factory.component.installer.step.database.DatabaseStep
import net.milosvasic.factory.component.installer.step.deploy.Deploy
import net.milosvasic.factory.component.installer.step.port.PortCheck
import net.milosvasic.factory.component.installer.step.reboot.Reboot
import net.milosvasic.factory.execution.flow.implementation.InstallationStepFlow
import net.milosvasic.factory.execution.flow.processing.ProcessingRecipesRegistration

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
            PortCheck::class -> {
                flow.registerRecipe(
                        PortCheck::class,
                        PortCheckRecipe::class
                )
                return true
            }
            Certificate::class -> {
                flow.registerRecipe(
                        Certificate::class,
                        CertificateRecipe::class
                )
            }
            TlsCertificate::class -> {
                flow.registerRecipe(
                        TlsCertificate::class,
                        CertificateRecipe::class
                )
            }
            DatabaseStep::class -> {
                flow.registerRecipe(
                        DatabaseStep::class,
                        DatabaseStepRecipe::class
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