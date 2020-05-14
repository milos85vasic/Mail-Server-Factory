package net.milosvasic.factory.mail.component.installer.step

import net.milosvasic.factory.mail.common.obtain.ObtainParametrized
import net.milosvasic.factory.mail.component.docker.step.DockerInstallationStepType
import net.milosvasic.factory.mail.component.docker.step.stack.Check
import net.milosvasic.factory.mail.component.docker.step.stack.SkipConditionCheck
import net.milosvasic.factory.mail.component.docker.step.stack.Stack
import net.milosvasic.factory.mail.component.docker.step.volume.Volume
import net.milosvasic.factory.mail.component.installer.step.condition.SkipCondition
import net.milosvasic.factory.mail.component.installer.step.deploy.Deploy
import net.milosvasic.factory.mail.component.installer.step.deploy.DeployValidator
import net.milosvasic.factory.mail.component.installer.step.reboot.Reboot
import net.milosvasic.factory.mail.component.packaging.item.Group
import net.milosvasic.factory.mail.component.packaging.item.Package
import net.milosvasic.factory.mail.configuration.InstallationStepDefinition
import net.milosvasic.factory.mail.validation.Validator

class InstallationStepFactory : ObtainParametrized<InstallationStepDefinition, InstallationStep<*>> {

    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    override fun obtain(vararg param: InstallationStepDefinition): InstallationStep<*> {

        Validator.Arguments.validateSingle(param)
        val definition = param[0]
        when (definition.type) {
            InstallationStepType.PACKAGE_GROUP.type -> {

                val group = Group(definition.getValue())
                return PackageManagerInstallationStep(listOf(group))
            }
            InstallationStepType.PACKAGES.type -> {

                val packages = mutableListOf<Package>()
                val split = definition.getValue().split(",")
                split.forEach {
                    packages.add(Package(it.trim()))
                }
                return PackageManagerInstallationStep(packages)
            }
            InstallationStepType.COMMAND.type -> {

                return CommandInstallationStep(definition.getValue())
            }
            InstallationStepType.REBOOT.type -> {

                return Reboot(definition.getValue().toInt())
            }
            InstallationStepType.SKIP_CONDITION.type -> {

                return SkipCondition(definition.getValue())
            }
            InstallationStepType.CONDITION_CHECK.type -> {

                return SkipConditionCheck(definition.getValue())
            }
            InstallationStepType.CHECK.type -> {

                return Check(definition.getValue())
            }
            InstallationStepType.DEPLOY.type -> {

                val validator = DeployValidator()
                if (validator.validate(definition.getValue())) {

                    val fromTo = definition.getValue().split(Deploy.delimiter)
                    val from = fromTo[0].trim()
                    val to = fromTo[1].trim()
                    return Deploy(from, to)
                } else {

                    throw IllegalArgumentException("Invalid deploy parameters")
                }
            }
            DockerInstallationStepType.VOLUME.type -> {

                return Volume(definition.getValue(), definition.name)
            }
            DockerInstallationStepType.STACK.type -> {

                return Stack(definition.getValue())
            }
        }
        throw IllegalArgumentException("Unknown installation step type: ${definition.type}")
    }
}