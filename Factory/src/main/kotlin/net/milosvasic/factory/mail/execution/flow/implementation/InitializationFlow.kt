package net.milosvasic.factory.mail.execution.flow.implementation

import net.milosvasic.factory.mail.common.CollectionWrapper
import net.milosvasic.factory.mail.common.Wrapper
import net.milosvasic.factory.mail.common.busy.BusyException
import net.milosvasic.factory.mail.component.Initialization
import net.milosvasic.factory.mail.execution.flow.FlowBuilder
import net.milosvasic.factory.mail.execution.flow.processing.FlowProcessingCallback

class InitializationFlow : FlowBuilder<Initialization, Unit, MutableList<Wrapper<Initialization>>>() {

    @Throws(BusyException::class)
    override fun width(subject: Initialization): InitializationFlow {
        super.width(subject)
        return this
    }

    override fun tryNext() {
        TODO("Not yet implemented")
    }

    override val subjects: CollectionWrapper<MutableList<Wrapper<Initialization>>>
        get() = TODO("Not yet implemented")

    override val processingCallback: FlowProcessingCallback
        get() = TODO("Not yet implemented")

    override fun process() {
        TODO("Not yet implemented")
    }

    override fun insertSubject() {
        TODO("Not yet implemented")
    }


}