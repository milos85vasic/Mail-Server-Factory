package net.milosvasic.factory.component.installer.recipe

import net.milosvasic.factory.component.installer.step.certificate.Certificate
import net.milosvasic.factory.component.installer.step.certificate.CertificateInitializationOperation
import net.milosvasic.factory.execution.flow.processing.FlowProcessingCallback
import net.milosvasic.factory.operation.OperationResult
import net.milosvasic.factory.operation.OperationResultListener

class CertificateRecipe : InstallationStepRecipe() {

    private val operationCallback = object : OperationResultListener {
        override fun onOperationPerformed(result: OperationResult) {
            when (result.operation) {
                is CertificateInitializationOperation -> {

                    step?.let {
                        (it as Certificate).unsubscribe(this)
                    }
                    callback?.onFinish(result.success)
                    callback = null
                }
            }
        }
    }

    @Throws(IllegalStateException::class, IllegalArgumentException::class)
    override fun process(callback: FlowProcessingCallback) {
        super.process(callback)
        step?.let { s ->
            if (s !is Certificate) {
                throw IllegalArgumentException("Unexpected installation step type: ${s::class.simpleName}")
            }
        }
        try {

            step?.let { s ->
                val step = s as Certificate
                try {
                    executeViaSSH(step, operationCallback)
                } catch (e: IllegalArgumentException) {

                    fail(e)
                }
            }
        } catch (e: IllegalStateException) {

            fail(e)
        } catch (e: IllegalArgumentException) {

            fail(e)
        }
    }
}