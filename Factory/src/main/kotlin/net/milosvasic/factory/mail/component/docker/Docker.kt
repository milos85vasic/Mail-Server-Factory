package net.milosvasic.factory.mail.component.docker

import net.milosvasic.factory.mail.EMPTY
import net.milosvasic.factory.mail.component.Toolkit
import net.milosvasic.factory.mail.component.docker.recipe.CheckRecipe
import net.milosvasic.factory.mail.component.docker.recipe.StackRecipe
import net.milosvasic.factory.mail.component.docker.recipe.VolumeRecipe
import net.milosvasic.factory.mail.component.docker.step.stack.Check
import net.milosvasic.factory.mail.component.docker.step.stack.SkipConditionCheck
import net.milosvasic.factory.mail.component.docker.step.stack.Stack
import net.milosvasic.factory.mail.component.docker.step.volume.Volume
import net.milosvasic.factory.mail.component.installer.InstallerAbstract
import net.milosvasic.factory.mail.component.installer.InstallerInitializationOperation
import net.milosvasic.factory.mail.component.installer.step.InstallationStep
import net.milosvasic.factory.mail.execution.flow.implementation.InstallationStepFlow
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.operation.OperationResultListener
import net.milosvasic.factory.mail.remote.Connection
import net.milosvasic.factory.mail.terminal.TerminalCommand
import java.util.concurrent.atomic.AtomicBoolean

class Docker(entryPoint: Connection) : InstallerAbstract(entryPoint) {

    private var command = String.EMPTY
    private val initialized = AtomicBoolean()

    private val listener = object : OperationResultListener {
        override fun onOperationPerformed(result: OperationResult) {
            when (result.operation) {
                is TerminalCommand -> {
                    entryPoint.unsubscribe(this)

                    free()
                    val installerInitializationOperation = DockerInitializationOperation()
                    val operationResult = OperationResult(installerInitializationOperation, result.success)
                    notify(operationResult)
                }
            }
        }
    }

    override fun initialization() {
        command = "${DockerCommand.DOCKER.obtain()} ${DockerCommand.VERSION.obtain()}"
        entryPoint.subscribe(listener)
        entryPoint.execute(TerminalCommand(command))
    }

    override fun termination() {
        initialized.set(false)
    }

    @Synchronized
    override fun isInitialized() = initialized.get()

    @Throws(IllegalArgumentException::class)
    override fun registerRecipes(step: InstallationStep<*>, flow: InstallationStepFlow) {
        super.registerRecipes(step, flow)
        when (step) {
            is Volume -> {
                flow.registerRecipe(
                        Volume::class,
                        VolumeRecipe::class
                )
            }
            is Stack -> {
                flow.registerRecipe(
                        Stack::class,
                        StackRecipe::class
                )
            }
            is Check -> {
                flow.registerRecipe(
                        Check::class,
                        CheckRecipe::class
                )
            }
            is SkipConditionCheck -> {
                flow.registerRecipe(
                        SkipConditionCheck::class,
                        CheckRecipe::class
                )
            }
        }
    }

    override fun getEnvironmentName() = "Docker"

    override fun getNotifyOperation() = DockerOperation()

    override fun getToolkit() = Toolkit(entryPoint)
}