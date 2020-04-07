package net.milosvasic.factory.mail.component.installer.step

import net.milosvasic.factory.mail.common.ObtainParametrized
import net.milosvasic.factory.mail.component.installer.step.condition.Condition
import net.milosvasic.factory.mail.component.installer.step.reboot.Reboot
import net.milosvasic.factory.mail.component.packaging.item.Group
import net.milosvasic.factory.mail.component.packaging.item.Package
import net.milosvasic.factory.mail.configuration.InstallationStepDefinition
import net.milosvasic.factory.mail.validation.Validator

class InstallationStepFactory : ObtainParametrized<InstallationStepDefinition, InstallationStep<*>> {

    @Throws(IllegalArgumentException::class)
    override fun obtain(vararg param: InstallationStepDefinition): InstallationStep<*> {

        Validator.Arguments.validateSingle(param)
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
            InstallationStepType.REBOOT.type -> {

                return Reboot(definition.value.toInt())
            }
            InstallationStepType.CONDITION.type -> {

                return Condition(
                    definition.value
                )
            }
        }
        throw IllegalArgumentException("Unknown installation step type: ${definition.type}")
    }
}