package net.milosvasic.factory.mail.component.packageManagement

import net.milosvasic.factory.mail.component.Component
import net.milosvasic.factory.mail.remote.ssh.SSH

abstract class PackageManager(private val entryPoint: SSH) : Component() {

    abstract val installCommand: String
    abstract val groupInstallCommand: String

    open fun install(packages: List<String>) {

    }

    open fun groupInstall(what: String) {

    }
}