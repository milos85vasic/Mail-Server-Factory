package net.milosvasic.factory.mail.test.implementation

import net.milosvasic.factory.mail.component.docker.step.stack.Check

class StubCheck : Check("Test", "echo 'stubCheck'")