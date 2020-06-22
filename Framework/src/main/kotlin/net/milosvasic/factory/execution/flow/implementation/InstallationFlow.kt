package net.milosvasic.factory.execution.flow.implementation

import net.milosvasic.factory.common.busy.BusyException
import net.milosvasic.factory.component.installer.InstallerAbstract
import net.milosvasic.factory.component.installer.InstallerOperation
import net.milosvasic.factory.configuration.SoftwareConfiguration
import net.milosvasic.factory.execution.flow.FlowBuilder
import net.milosvasic.factory.execution.flow.FlowSimpleBuilder
import net.milosvasic.factory.execution.flow.callback.FlowCallback
import net.milosvasic.factory.execution.flow.processing.FlowProcessingCallback
import net.milosvasic.factory.execution.flow.processing.ProcessingRecipe
import net.milosvasic.factory.log
import net.milosvasic.factory.operation.OperationResult
import net.milosvasic.factory.operation.OperationResultListener

class InstallationFlow(private val installer: InstallerAbstract) : FlowSimpleBuilder<SoftwareConfiguration, String>() {

    @Throws(BusyException::class)
    override fun width(subject: SoftwareConfiguration): InstallationFlow {
        super.width(subject)
        return this
    }

    @Throws(BusyException::class)
    override fun onFinish(callback: FlowCallback): InstallationFlow {
        super.onFinish(callback)
        return this
    }

    @Throws(BusyException::class)
    override fun connect(flow: FlowBuilder<*, *, *>): InstallationFlow {
        super.connect(flow)
        return this
    }

    @Throws(IllegalArgumentException::class)
    override fun getProcessingRecipe(subject: SoftwareConfiguration): ProcessingRecipe {

        return object : ProcessingRecipe {

            private var callback: FlowProcessingCallback? = null

            private val operationCallback = object : OperationResultListener {
                override fun onOperationPerformed(result: OperationResult) {

                    installer.unsubscribe(this)
                    when (result.operation) {
                        is InstallerOperation -> {

                            callback?.onFinish(result.success)
                            callback = null
                        }
                    }
                }
            }

            override fun process(callback: FlowProcessingCallback) {
                this.callback = callback
                try {
                    installer.setConfiguration(subject)
                    installer.subscribe(operationCallback)
                    installer.install()
                } catch (e: BusyException) {

                    installer.unsubscribe(operationCallback)
                    log.e(e)
                    callback.onFinish(false)
                    this.callback = null
                }
            }
        }
    }
}