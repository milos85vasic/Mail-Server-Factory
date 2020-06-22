package net.milosvasic.factory.test.implementation

import net.milosvasic.factory.component.docker.step.stack.SkipConditionCheck
import net.milosvasic.factory.terminal.command.EchoCommand
import net.milosvasic.factory.terminal.command.TestCommand

class StubSkipConditionCheck(positive: Boolean) : SkipConditionCheck(
        "Test",
        if (positive) {
            EchoCommand("stubConditionCheck")
        } else {
            TestCommand("./stubConditionCheck_FileDoesNotExist")
        }
)