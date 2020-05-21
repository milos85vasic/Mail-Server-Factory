package net.milosvasic.factory.mail.test.implementation

import net.milosvasic.factory.mail.terminal.TerminalCommand
import net.milosvasic.factory.mail.terminal.WrappedTerminalCommand

class StubSSHWrappedCommand(command: TerminalCommand, execute: String) : WrappedTerminalCommand(command, execute)