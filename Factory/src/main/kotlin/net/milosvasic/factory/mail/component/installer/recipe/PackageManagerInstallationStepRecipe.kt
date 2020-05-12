package net.milosvasic.factory.mail.component.installer.recipe

import net.milosvasic.factory.mail.component.installer.step.PackageManagerInstallationStep
import net.milosvasic.factory.mail.component.packaging.PackageInstaller
import net.milosvasic.factory.mail.component.packaging.PackageManagerOperation
import net.milosvasic.factory.mail.execution.flow.processing.FlowProcessingCallback
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.operation.OperationResultListener

class PackageManagerInstallationStepRecipe : InstallationStepRecipe<PackageInstaller>() {

    private val operationCallback = object : OperationResultListener {
        override fun onOperationPerformed(result: OperationResult) {
            entryPoint?.unsubscribe(this)
            when (result.operation) {
                is PackageManagerOperation -> {

                    callback?.onFinish(result.success, getErrorMessage(result))
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
        step?.let { s ->
            if (s !is PackageManagerInstallationStep) {
                throw IllegalArgumentException("Unexpected installation step type: ${s::class.simpleName}")
            }
        }

        try {

            entryPoint?.let { entry ->
                step?.let { s ->
                    entry.subscribe(operationCallback)
                    (s as PackageManagerInstallationStep).execute(entry)
                }
            }
        } catch (e: IllegalStateException) {

            fail(e)
        } catch (e: IllegalArgumentException) {

            fail(e)
        }
    }
}