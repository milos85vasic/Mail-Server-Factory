package net.milosvasic.factory.mail.execution.flow

import net.milosvasic.factory.mail.common.Executor
import net.milosvasic.factory.mail.terminal.TerminalCommand

class CommandsFlow : Flow<Executor<TerminalCommand>, TerminalCommand>() {

    override fun getProcessingRecipe(
            subject: Executor<TerminalCommand>,
            operation: TerminalCommand
    ): ProcessingRecipe {

        TODO("Not yet implemented")
    }
}