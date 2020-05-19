package net.milosvasic.factory.mail.component.packaging

import net.milosvasic.factory.mail.remote.Connection

open class Yum(entryPoint: Connection) : PackageManager(entryPoint) {

    override val applicationBinaryName: String
        get() = "yum"
}