package net.milosvasic.factory.component.packaging

import net.milosvasic.factory.remote.Connection

open class Yum(entryPoint: Connection) : PackageManager(entryPoint) {

    override val applicationBinaryName: String
        get() = "yum"
}