package net.milosvasic.factory.mail.test.implementation

import net.milosvasic.factory.mail.component.docker.step.stack.SkipConditionCheck

class StubSkipConditionCheck(positive: Boolean) : SkipConditionCheck(
        "Test",
        if (positive) {
            "echo 'stubConditionCheck'"
        } else {
            "test -e ./stubConditionCheck_FileDoesNotExist"
        }
)