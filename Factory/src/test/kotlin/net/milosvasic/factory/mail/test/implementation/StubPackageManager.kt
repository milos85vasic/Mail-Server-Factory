package net.milosvasic.factory.mail.test.implementation

import net.milosvasic.factory.mail.component.packaging.PackageManager
import net.milosvasic.factory.mail.remote.Connection

class StubPackageManager(entryPoint: Connection) : PackageManager(entryPoint) {

    override val applicationBinaryName: String
        get() = "echo"
}