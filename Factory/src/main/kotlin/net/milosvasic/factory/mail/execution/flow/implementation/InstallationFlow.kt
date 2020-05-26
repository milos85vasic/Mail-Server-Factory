package net.milosvasic.factory.mail.execution.flow.implementation

import net.milosvasic.factory.mail.EMPTY
import net.milosvasic.factory.mail.common.busy.BusyException
import net.milosvasic.factory.mail.common.obtain.Obtain
import net.milosvasic.factory.mail.component.installer.InstallerAbstract
import net.milosvasic.factory.mail.component.installer.InstallerOperation
import net.milosvasic.factory.mail.configuration.SoftwareConfiguration
import net.milosvasic.factory.mail.execution.flow.FlowBuilder
import net.milosvasic.factory.mail.execution.flow.FlowSimpleBuilder
import net.milosvasic.factory.mail.execution.flow.callback.FlowCallback
import net.milosvasic.factory.mail.execution.flow.processing.FlowProcessingCallback
import net.milosvasic.factory.mail.execution.flow.processing.ProcessingRecipe
import net.milosvasic.factory.mail.getMessage
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.operation.OperationResultListener

class InstallationFlow(private val installer: InstallerAbstract) : FlowSimpleBuilder<SoftwareConfiguration, String>() {

    @Throws(BusyException::class)
    override fun width(subject: SoftwareConfiguration): InstallationFlow {
        super.width(subject)
        return this
    }

    @Throws(BusyException::class)
    override fun onFinish(callback: FlowCallback<String>): InstallationFlow {
        super.onFinish(callback)
        return this
    }

    @Throws(BusyException::class)
    override fun connect(flow: FlowBuilder<*, *, *>): InstallationFlow {
        super.connect(flow)
        return this
    }

    @Throws(BusyException::class)
    override fun connect(provider: Obtain<FlowBuilder<*, *, *>>): InstallationFlow {
        super.connect(provider)
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
                            val message = if (result.success) {
                                String.EMPTY
                            } else {
                                "Installation failed for $subject"
                            }
                            callback?.onFinish(result.success, message)
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
                    callback.onFinish(false, e.getMessage())
                }
            }
        }
    }
}