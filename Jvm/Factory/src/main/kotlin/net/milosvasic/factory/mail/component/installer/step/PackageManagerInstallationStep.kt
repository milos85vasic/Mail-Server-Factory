package net.milosvasic.factory.mail.component.installer.step

import net.milosvasic.factory.mail.component.packaging.PackageInstaller
import net.milosvasic.factory.mail.component.packaging.item.InstallationItem

class PackageManagerInstallationStep(private val toInstall: List<InstallationItem>) :
    InstallationStep<PackageInstaller>() {

    @Synchronized
    @Throws(IllegalStateException::class, IllegalArgumentException::class)
    override fun execute(vararg params: PackageInstaller) {

        if (params.size > 1 || params.isEmpty()) {
            throw IllegalArgumentException("Expected 1 argument")
        }
        val installer = params[0]
        installer.install(*toInstall.toTypedArray())
    }
}