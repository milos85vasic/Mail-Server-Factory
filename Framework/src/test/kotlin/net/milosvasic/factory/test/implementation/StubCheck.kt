package net.milosvasic.factory.test.implementation

import net.milosvasic.factory.component.docker.step.stack.Check
import net.milosvasic.factory.terminal.command.EchoCommand

class StubCheck : Check("Test", EchoCommand("stubCheck"))