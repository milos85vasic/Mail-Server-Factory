package net.milosvasic.factory.mail.containing

import net.milosvasic.factory.mail.component.Termination
import net.milosvasic.factory.mail.component.SystemComponent
import net.milosvasic.factory.mail.remote.ssh.SSH

abstract class ContainerSystem(private val entryPoint: SSH) : SystemComponent(), Termination