package net.milosvasic.factory.test.implementation

import net.milosvasic.factory.component.packaging.PackageManager
import net.milosvasic.factory.remote.Connection

class StubPackageManager(entryPoint: Connection) : PackageManager(entryPoint) {

    override val applicationBinaryName: String
        get() = "echo"
}