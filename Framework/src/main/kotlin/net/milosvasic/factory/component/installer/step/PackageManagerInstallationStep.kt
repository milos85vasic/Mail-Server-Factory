package net.milosvasic.factory.component.installer.step

import net.milosvasic.factory.component.packaging.PackageInstaller
import net.milosvasic.factory.component.packaging.item.InstallationItem
import net.milosvasic.factory.validation.Validator

class PackageManagerInstallationStep(private val toInstall: List<InstallationItem>) :
    InstallationStep<PackageInstaller>() {

    @Synchronized
    @Throws(IllegalStateException::class, IllegalArgumentException::class)
    override fun execute(vararg params: PackageInstaller) {

        Validator.Arguments.validateSingle(params)
        val installer = params[0]
        installer.install(*toInstall.toTypedArray())
    }
}