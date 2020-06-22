package net.milosvasic.factory.execution.flow.processing

import net.milosvasic.factory.component.installer.step.InstallationStep
import net.milosvasic.factory.execution.flow.implementation.InstallationStepFlow

interface ProcessingRecipesRegistration {

    fun registerRecipes(step: InstallationStep<*>, flow: InstallationStepFlow): Boolean
}