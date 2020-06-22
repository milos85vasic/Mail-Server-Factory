package net.milosvasic.factory.component.installer.recipe

import net.milosvasic.factory.component.installer.step.PackageManagerInstallationStep
import net.milosvasic.factory.component.packaging.PackageManagerOperation
import net.milosvasic.factory.execution.flow.processing.FlowProcessingCallback
import net.milosvasic.factory.operation.OperationResult
import net.milosvasic.factory.operation.OperationResultListener

class PackageManagerInstallationStepRecipe : InstallationStepRecipe() {

    private val operationCallback = object : OperationResultListener {
        override fun onOperationPerformed(result: OperationResult) {
            toolkit?.packageInstaller?.unsubscribe(this)
            when (result.operation) {
                is PackageManagerOperation -> {

                    callback?.onFinish(result.success)
                    callback = null
                }
            }
        }
    }

    @Throws(IllegalStateException::class, IllegalArgumentException::class)
    override fun process(callback: FlowProcessingCallback) {
        super.process(callback)
        val validator = InstallationStepRecipeValidator()
        if (!validator.validate(this)) {
            throw IllegalArgumentException("Invalid installation step recipe: $this")
        }
        if (toolkit?.packageInstaller == null) {
            throw IllegalArgumentException("Package installer not provided")
        }
        step?.let { s ->
            if (s !is PackageManagerInstallationStep) {
                throw IllegalArgumentException("Unexpected installation step type: ${s::class.simpleName}")
            }
        }

        try {

            toolkit?.let { tools ->
                step?.let { s ->
                    val step = s as PackageManagerInstallationStep
                    tools.packageInstaller?.let { packageInstaller ->
                        packageInstaller.subscribe(operationCallback)
                        step.execute(packageInstaller)
                    }
                }
            }
        } catch (e: IllegalStateException) {

            fail(e)
        } catch (e: IllegalArgumentException) {

            fail(e)
        }
    }
}