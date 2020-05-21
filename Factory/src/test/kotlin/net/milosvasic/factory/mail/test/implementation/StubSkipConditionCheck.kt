package net.milosvasic.factory.mail.test.implementation

import net.milosvasic.factory.mail.component.docker.step.stack.SkipConditionCheck
import net.milosvasic.factory.mail.terminal.command.EchoCommand
import net.milosvasic.factory.mail.terminal.command.TestCommand

class StubSkipConditionCheck(positive: Boolean) : SkipConditionCheck(
        "Test",
        if (positive) {
            EchoCommand("stubConditionCheck")
        } else {
            TestCommand("./stubConditionCheck_FileDoesNotExist")
        }
)