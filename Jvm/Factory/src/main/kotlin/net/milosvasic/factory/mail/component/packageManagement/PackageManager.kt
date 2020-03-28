package net.milosvasic.factory.mail.component.packageManagement

import net.milosvasic.factory.mail.component.Component
import net.milosvasic.factory.mail.remote.ssh.SSH

abstract class PackageManager(private val entryPoint: SSH) : Component()