package net.milosvasic.factory.mail.component.installer.step

import net.milosvasic.factory.mail.common.ObtainParametrized
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

            }
            InstallationStepType.PACKAGES.type -> {

            }
            InstallationStepType.COMMAND.type -> {

            }
        }
        throw IllegalArgumentException("Unknown type: ${definition.type}")
    }
}