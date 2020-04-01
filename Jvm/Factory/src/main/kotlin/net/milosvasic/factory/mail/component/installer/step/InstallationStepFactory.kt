package net.milosvasic.factory.mail.component.installer.step

import net.milosvasic.factory.mail.common.ObtainParametrized
import net.milosvasic.factory.mail.component.packaging.item.Group
import net.milosvasic.factory.mail.component.packaging.item.Package
import net.milosvasic.factory.mail.configuration.InstallationStepDefinition

class InstallationStepFactory : ObtainParametrized<InstallationStepDefinition, InstallationStep> {

    @Throws(IllegalArgumentException::class)
    override fun obtain(vararg param: InstallationStepDefinition): InstallationStep {

        if (param.size > 1 || param.isEmpty()) {
            throw IllegalArgumentException("Expected 1 argument")
        }
        val definition = param[0]
        when (definition.type) {
            InstallationStepType.PACKAGE_GROUP.type -> {

                val group = Group(definition.value)
                return PackageManagerInstallationStep(listOf(group))
            }
            InstallationStepType.PACKAGES.type -> {

                val packages = mutableListOf<Package>()
                val split = definition.value.split(",")
                split.forEach {
                    packages.add(Package(it.trim()))
                }
                return PackageManagerInstallationStep(packages)
            }
            InstallationStepType.COMMAND.type -> {

                return CommandInstallationStep(definition.value)
            }
        }
        throw IllegalArgumentException("Unknown type: ${definition.type}")
    }
}