package net.milosvasic.factory.component.packaging

import net.milosvasic.factory.remote.Connection

class AptGet(entryPoint: Connection) : PackageManager(entryPoint) {

    override val applicationBinaryName: String
        get() = "apt-get"

    override fun installCommand(): String {
        return "export DEBIAN_FRONTEND=noninteractive; " + super.installCommand()
    }

    override fun uninstallCommand(): String {
        return "export DEBIAN_FRONTEND=noninteractive; " + super.uninstallCommand()
    }

    override fun groupInstallCommand(): String {
        return "export DEBIAN_FRONTEND=noninteractive; " + super.groupInstallCommand()
    }

    override fun groupUninstallCommand(): String {
        return "export DEBIAN_FRONTEND=noninteractive; " + super.groupUninstallCommand()
    }
}