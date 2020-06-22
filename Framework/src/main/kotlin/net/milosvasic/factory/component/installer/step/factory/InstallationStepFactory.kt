package net.milosvasic.factory.component.installer.step.factory

import net.milosvasic.factory.common.obtain.ObtainParametrized
import net.milosvasic.factory.component.installer.step.InstallationStep
import net.milosvasic.factory.configuration.InstallationStepDefinition

interface InstallationStepFactory : ObtainParametrized<InstallationStepDefinition, InstallationStep<*>>