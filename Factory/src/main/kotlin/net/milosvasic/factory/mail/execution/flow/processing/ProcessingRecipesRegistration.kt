package net.milosvasic.factory.mail.execution.flow.processing

import net.milosvasic.factory.mail.component.installer.step.InstallationStep
import net.milosvasic.factory.mail.execution.flow.implementation.InstallationStepFlow

interface ProcessingRecipesRegistration {

    fun registerRecipes(step: InstallationStep<*>, flow: InstallationStepFlow): Boolean
}