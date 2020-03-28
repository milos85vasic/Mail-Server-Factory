package net.milosvasic.factory.mail.component.packageManagement

import net.milosvasic.factory.mail.component.Component
import net.milosvasic.factory.mail.remote.ssh.SSH

abstract class PackageManager(private val entryPoint: SSH) : Component() {

    abstract val installCommand: String
    abstract val groupInstallCommand: String

    open fun install(packages: List<String>) {
        var cmd = installCommand
        packages.forEach {
            cmd += " $it"
        }
    }

    open fun groupInstall(what: String) {
        val cmd = groupInstallCommand

    }
}