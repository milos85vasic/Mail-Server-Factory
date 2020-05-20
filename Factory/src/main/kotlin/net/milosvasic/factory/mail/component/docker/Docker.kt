package net.milosvasic.factory.mail.component.docker

import net.milosvasic.factory.mail.component.Toolkit
import net.milosvasic.factory.mail.component.docker.recipe.StackRecipe
import net.milosvasic.factory.mail.component.docker.step.stack.Check
import net.milosvasic.factory.mail.component.docker.step.stack.SkipConditionCheck
import net.milosvasic.factory.mail.component.docker.step.stack.Stack
import net.milosvasic.factory.mail.component.installer.InstallerAbstract
import net.milosvasic.factory.mail.component.installer.recipe.CommandInstallationStepRecipe
import net.milosvasic.factory.mail.component.installer.recipe.ConditionRecipe
import net.milosvasic.factory.mail.component.installer.step.InstallationStep
import net.milosvasic.factory.mail.execution.flow.implementation.InstallationStepFlow
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.remote.Connection
import java.util.concurrent.atomic.AtomicBoolean

class Docker(entryPoint: Connection) : InstallerAbstract(entryPoint) {

    private val initialized = AtomicBoolean()

    override fun initialization() {

        initialized.set(true)
        free()
        val installerInitializationOperation = DockerInitializationOperation()
        val operationResult = OperationResult(installerInitializationOperation, true)
        notify(operationResult)
    }

    override fun termination() {
        initialized.set(false)
    }

    @Synchronized
    override fun isInitialized() = initialized.get()

    @Throws(IllegalArgumentException::class)
    override fun registerRecipes(step: InstallationStep<*>, flow: InstallationStepFlow) {
        when (step) {
            is Stack -> {
                flow.registerRecipe(
                        Stack::class,
                        StackRecipe::class
                )
                return
            }
            is Check -> {
                flow.registerRecipe(
                        Check::class,
                        CommandInstallationStepRecipe::class
                )
                return
            }
            is SkipConditionCheck -> {
                flow.registerRecipe(
                        SkipConditionCheck::class,
                        ConditionRecipe::class
                )
                return
            }
        }
        super.registerRecipes(step, flow)
    }

    override fun getEnvironmentName() = "Docker"

    override fun getToolkit() = Toolkit(entryPoint)

    override fun getNotifyOperation() = DockerOperation()
}