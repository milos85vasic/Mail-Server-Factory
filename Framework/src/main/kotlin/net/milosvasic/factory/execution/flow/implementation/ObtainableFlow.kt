package net.milosvasic.factory.execution.flow.implementation

import net.milosvasic.factory.common.busy.BusyException
import net.milosvasic.factory.common.obtain.Obtain
import net.milosvasic.factory.execution.flow.FlowBuilder
import net.milosvasic.factory.execution.flow.FlowSimpleBuilder
import net.milosvasic.factory.execution.flow.callback.FlowCallback
import net.milosvasic.factory.execution.flow.processing.FlowProcessingCallback
import net.milosvasic.factory.execution.flow.processing.ProcessingRecipe
import net.milosvasic.factory.log

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
                override fun onFinish(success: Boolean) {

                    callback?.onFinish(success)
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

                    log.e(e)
                    callback.onFinish(false)
                    this.callback = null
                } catch (e: IllegalStateException) {

                    log.e(e)
                    callback.onFinish(false)
                    this.callback = null
                }
            }
        }
    }
}