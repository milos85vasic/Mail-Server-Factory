package net.milosvasic.factory.mail.component.packageManagement

import net.milosvasic.factory.mail.remote.ssh.SSH

class Dnf(private val entryPoint: SSH) : Yum(entryPoint) {

    override val installCommand: String
        get() = "dnf install -y"

    override val groupInstallCommand: String
        get() = "dnf groupinstall -y"
}