package net.milosvasic.factory.mail.test.implementation

import net.milosvasic.factory.mail.component.docker.step.stack.Check
import net.milosvasic.factory.mail.component.installer.step.CommandInstallationStep

class StubCheck : Check("Test", "echo 'stubCheck'")