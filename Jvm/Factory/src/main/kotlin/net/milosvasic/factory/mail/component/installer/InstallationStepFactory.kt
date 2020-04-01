package net.milosvasic.factory.mail.component.installer

import net.milosvasic.factory.mail.common.ObtainParametrized
import net.milosvasic.factory.mail.configuration.InstallationStepDefinition

class InstallationStepFactory : ObtainParametrized<InstallationStepDefinition, InstallationStep> {

    @Throws(IllegalArgumentException::class)
    override fun obtain(vararg param: InstallationStepDefinition): InstallationStep {

        // TODO: Implement.
        throw IllegalArgumentException()
    }
}