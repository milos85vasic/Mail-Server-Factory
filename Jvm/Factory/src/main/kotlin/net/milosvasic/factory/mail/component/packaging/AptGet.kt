package net.milosvasic.factory.mail.component.packaging

import net.milosvasic.factory.mail.remote.ssh.SSH

class AptGet(entryPoint: SSH) : PackageManager(entryPoint) {

    override val applicationBinaryName: String
        get() = "apt-get"
}