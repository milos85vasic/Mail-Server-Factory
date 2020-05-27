package net.milosvasic.factory.mail.execution.flow.implementation

import net.milosvasic.factory.mail.common.busy.BusyException
import net.milosvasic.factory.mail.common.obtain.Obtain
import net.milosvasic.factory.mail.execution.flow.FlowBuilder
import net.milosvasic.factory.mail.execution.flow.FlowSimpleBuilder
import net.milosvasic.factory.mail.execution.flow.callback.FlowCallback
import net.milosvasic.factory.mail.execution.flow.processing.FlowProcessingCallback
import net.milosvasic.factory.mail.execution.flow.processing.ProcessingRecipe
import net.milosvasic.factory.mail.getMessage

class ObtainableFlow : FlowSimpleBuilder<Obtain<FlowBuilder<*, *, *>>, String>() {

    @Throws(BusyException::class, IllegalArgumentException::class)
    override fun width(subject: Obtain<FlowBuilder<*, *, *>>): ObtainableFlow {
        if (subjects.get().isEmpty()) {
            super.width(subject)
            return this
        } else {

            throw IllegalArgumentException("Subject already added")
        }
    }

    override fun onFinish(callback: FlowCallback): ObtainableFlow {
        super.onFinish(callback)
        return this
    }

    @Throws(BusyException::class)
    override fun connect(flow: FlowBuilder<*, *, *>): ObtainableFlow {
        super.connect(flow)
        return this
    }

    override fun getProcessingRecipe(subject: Obtain<FlowBuilder<*, *, *>>): ProcessingRecipe {

        return object : ProcessingRecipe {

            private var callback: FlowProcessingCallback? = null

            private val flowCallback = object : FlowCallback {
                override fun onFinish(success: Boolean, message: String) {

                    callback?.onFinish(success, message)
                    callback = null
                }
            }

            override fun process(callback: FlowProcessingCallback) {
                this.callback = callback
                try {
                    subject.obtain()
                            .onFinish(flowCallback)
                            .run()
                } catch (e: IllegalArgumentException) {

                    callback.onFinish(false, e.getMessage())
                    this.callback = null
                } catch (e: IllegalStateException) {

                    callback.onFinish(false, e.getMessage())
                    this.callback = null
                }
            }
        }
    }
}