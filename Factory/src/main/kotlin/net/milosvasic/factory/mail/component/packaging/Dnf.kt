package net.milosvasic.factory.mail.component.packaging

import net.milosvasic.factory.mail.remote.ssh.SSH

class Dnf(entryPoint: SSH) : Yum(entryPoint) {

    override val applicationBinaryName: String
        get() = "dnf"
}