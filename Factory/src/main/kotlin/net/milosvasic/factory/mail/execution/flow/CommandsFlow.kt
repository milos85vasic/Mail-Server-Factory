package net.milosvasic.factory.mail.execution.flow

import net.milosvasic.factory.mail.common.Executor
import net.milosvasic.factory.mail.common.busy.BusyException
import net.milosvasic.factory.mail.terminal.TerminalCommand

class CommandsFlow : FlowBuilder<Executor<TerminalCommand>, TerminalCommand>() {

    @Throws(BusyException::class)
    override fun width(subject: Executor<TerminalCommand>): CommandsFlow {
        super.width(subject)
        return this
    }

    @Throws(BusyException::class)
    fun perform(what: String): CommandsFlow {
        perform(TerminalCommand(what))
        return this
    }

    @Throws(BusyException::class)
    override fun perform(what: TerminalCommand): CommandsFlow {
        super.perform(what)
        return this
    }

    @Throws(BusyException::class)
    override fun onFinish(callback: FlowCallback): CommandsFlow {
        super.onFinish(callback)
        return this
    }

    override fun getProcessingRecipe(
            subject: Executor<TerminalCommand>,
            operation: TerminalCommand
    ): ProcessingRecipe {

        TODO("Not yet implemented")
    }
}