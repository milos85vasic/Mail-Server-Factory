package net.milosvasic.factory.mail.component.packageManagement

import net.milosvasic.factory.mail.remote.ssh.SSH

class Dnf(private val entryPoint: SSH) : Yum(entryPoint) {

    override val installCommand: String
        get() = TODO("Not yet implemented")

    override fun install(packages: List<String>) {
        super.install(packages)
    }

    override fun groupInstall(what: String) {
        TODO("Not yet implemented")
    }
}