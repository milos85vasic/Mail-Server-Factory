package net.milosvasic.factory.mail.component.installer.step

import net.milosvasic.factory.mail.component.packaging.PackageManager
import net.milosvasic.factory.mail.component.packaging.item.InstallationItem

class PackageManagerInstallationStep(private val toInstall: List<InstallationItem>) :
    InstallationStep<PackageManager>() {

    @Synchronized
    @Throws(IllegalArgumentException::class)
    override fun execute(vararg params: PackageManager) {

        if (params.size > 1 || params.isEmpty()) {
            throw IllegalArgumentException("Expected 1 argument")
        }
        val manager = params[0]

        // TODO: Iterate busy item by item from toInstall
    }
}