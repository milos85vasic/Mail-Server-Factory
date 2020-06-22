package net.milosvasic.factory.terminal

open class WrappedTerminalCommand(
        val wrappedCommand: TerminalCommand,
        wrappedToExecute: String = wrappedCommand.command

) : TerminalCommand(wrappedToExecute, wrappedCommand.configuration)