package net.milosvasic.factory.mail.component.packaging

import net.milosvasic.factory.mail.remote.ssh.SSH

open class Yum(entryPoint: SSH) : PackageManager(entryPoint) {

    override val applicationBinaryName: String
        get() = "yum"
}