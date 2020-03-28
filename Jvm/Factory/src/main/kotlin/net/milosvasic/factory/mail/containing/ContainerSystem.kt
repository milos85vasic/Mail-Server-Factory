package net.milosvasic.factory.mail.containing

import net.milosvasic.factory.mail.common.Installation
import net.milosvasic.factory.mail.component.Component
import net.milosvasic.factory.mail.remote.ssh.SSH

abstract class ContainerSystem(private val entryPoint: SSH) : Installation,
    Component