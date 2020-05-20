package net.milosvasic.factory.mail.component.installer.step.factory

import net.milosvasic.factory.mail.common.obtain.ObtainParametrized
import net.milosvasic.factory.mail.component.installer.step.InstallationStep
import net.milosvasic.factory.mail.configuration.InstallationStepDefinition

interface InstallationStepFactory : ObtainParametrized<InstallationStepDefinition, InstallationStep<*>>