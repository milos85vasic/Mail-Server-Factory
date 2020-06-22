package net.milosvasic.factory.test.implementation

import net.milosvasic.factory.terminal.TerminalCommand
import net.milosvasic.factory.terminal.WrappedTerminalCommand

class StubSSHWrappedCommand(command: TerminalCommand, execute: String) : WrappedTerminalCommand(command, execute)