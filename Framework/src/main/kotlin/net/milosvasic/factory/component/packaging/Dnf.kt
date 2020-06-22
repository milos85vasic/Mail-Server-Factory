package net.milosvasic.factory.component.packaging

import net.milosvasic.factory.remote.Connection

class Dnf(entryPoint: Connection) : Yum(entryPoint) {

    override val applicationBinaryName: String
        get() = "dnf"
}