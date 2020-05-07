package net.milosvasic.factory.mail.execution.flow.implementation

import net.milosvasic.factory.mail.common.busy.BusyException
import net.milosvasic.factory.mail.component.Initialization
import net.milosvasic.factory.mail.execution.flow.FlowBuilder
import net.milosvasic.factory.mail.execution.flow.processing.ProcessingRecipe

class InitializationFlow : FlowBuilder<Initialization, Unit, String>() {

    @Throws(BusyException::class)
    override fun width(subject: Initialization): InitializationFlow {
        super.width(subject)
        return this
    }

    override fun tryNext() {
        TODO("Not yet implemented")
    }

    override fun getProcessingRecipe(subject: Initialization, operation: Unit): ProcessingRecipe {
        TODO("Not yet implemented")
    }
}