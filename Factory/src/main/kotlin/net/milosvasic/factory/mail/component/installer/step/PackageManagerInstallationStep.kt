package net.milosvasic.factory.mail.component.installer.step

import net.milosvasic.factory.mail.component.packaging.PackageInstaller
import net.milosvasic.factory.mail.component.packaging.item.InstallationItem
import net.milosvasic.factory.mail.validation.Validator

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