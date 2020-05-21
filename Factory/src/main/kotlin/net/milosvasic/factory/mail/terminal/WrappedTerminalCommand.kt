package net.milosvasic.factory.mail.terminal

open class WrappedTerminalCommand(
        val wrappedCommand: TerminalCommand,
        val wrappedToExecute: String = wrappedCommand.command

) : TerminalCommand(wrappedToExecute, wrappedCommand.configuration)