package net.milosvasic.factory.mail.test.implementation

import net.milosvasic.factory.mail.component.docker.step.stack.Check
import net.milosvasic.factory.mail.terminal.command.EchoCommand

class StubCheck : Check("Test", EchoCommand("stubCheck"))