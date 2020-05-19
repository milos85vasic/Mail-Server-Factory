package net.milosvasic.factory.mail.component.packaging

import net.milosvasic.factory.mail.remote.Connection

class Dnf(entryPoint: Connection) : Yum(entryPoint) {

    override val applicationBinaryName: String
        get() = "dnf"
}