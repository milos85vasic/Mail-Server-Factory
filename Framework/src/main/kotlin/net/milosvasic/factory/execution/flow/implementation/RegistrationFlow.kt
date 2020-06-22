package net.milosvasic.factory.execution.flow.implementation

import net.milosvasic.factory.common.Registration
import net.milosvasic.factory.common.busy.BusyException
import net.milosvasic.factory.common.obtain.Obtain
import net.milosvasic.factory.execution.flow.FlowBuilder
import net.milosvasic.factory.execution.flow.FlowPerformBuilder
import net.milosvasic.factory.execution.flow.callback.FlowCallback
import net.milosvasic.factory.execution.flow.processing.FlowProcessingCallback
import net.milosvasic.factory.execution.flow.processing.ProcessingRecipe
import net.milosvasic.factory.log

class RegistrationFlow<T> : FlowPerformBuilder<Registration<T>, T, String>() {

    @Throws(BusyException::class)
    override fun width(subject: Registration<T>): RegistrationFlow<T> {
        super.width(subject)
        return this
    }

    @Throws(BusyException::class)
    override fun perform(what: T): RegistrationFlow<T> {
        super.perform(what)
        return this
    }

    @Throws(BusyException::class)
    override fun perform(what: Obtain<T>): RegistrationFlow<T> {
        super.perform(what)
        return this
    }

    @Throws(BusyException::class)
    override fun onFinish(callback: FlowCallback): RegistrationFlow<T> {
        super.onFinish(callback)
        return this
    }

    @Throws(BusyException::class)
    override fun connect(flow: FlowBuilder<*, *, *>): RegistrationFlow<T> {
        super.connect(flow)
        return this
    }

    @Throws(IllegalArgumentException::class)
    override fun getProcessingRecipe(subject: Registration<T>, operation: T): ProcessingRecipe {

        return object : ProcessingRecipe {

            override fun process(callback: FlowProcessingCallback) {
                try {
                    subject.register(operation)
                    callback.onFinish(true)
                } catch (e: Exception) {

                    log.e(e)
                    callback.onFinish(false)
                }
            }
        }
    }
}