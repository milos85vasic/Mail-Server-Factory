package net.milosvasic.factory.mail.component.installer.step.factory

import net.milosvasic.factory.mail.component.docker.step.DockerInstallationStepType
import net.milosvasic.factory.mail.component.docker.step.dockerfile.Build
import net.milosvasic.factory.mail.component.docker.step.stack.Check
import net.milosvasic.factory.mail.component.docker.step.stack.SkipConditionCheck
import net.milosvasic.factory.mail.component.docker.step.stack.Stack
import net.milosvasic.factory.mail.component.installer.step.CommandInstallationStep
import net.milosvasic.factory.mail.component.installer.step.InstallationStep
import net.milosvasic.factory.mail.component.installer.step.InstallationStepType
import net.milosvasic.factory.mail.component.installer.step.PackageManagerInstallationStep
import net.milosvasic.factory.mail.component.installer.step.condition.Condition
import net.milosvasic.factory.mail.component.installer.step.condition.SkipCondition
import net.milosvasic.factory.mail.component.installer.step.database.DatabaseStep
import net.milosvasic.factory.mail.component.installer.step.deploy.Deploy
import net.milosvasic.factory.mail.component.installer.step.deploy.DeployValidator
import net.milosvasic.factory.mail.component.installer.step.reboot.Reboot
import net.milosvasic.factory.mail.component.packaging.item.Group
import net.milosvasic.factory.mail.component.packaging.item.Package
import net.milosvasic.factory.mail.configuration.InstallationStepDefinition
import net.milosvasic.factory.mail.terminal.command.RawTerminalCommand
import net.milosvasic.factory.mail.validation.Validator

class MainInstallationStepFactory : InstallationStepFactory {

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

                return CommandInstallationStep(RawTerminalCommand(definition.getValue()))
            }
            InstallationStepType.REBOOT.type -> {

                return Reboot(definition.getValue().toInt())
            }
            InstallationStepType.CONDITION.type -> {

                return Condition(RawTerminalCommand(definition.getValue()))
            }
            InstallationStepType.SKIP_CONDITION.type -> {

                return SkipCondition(RawTerminalCommand(definition.getValue()))
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
            InstallationStepType.DATABASE.type -> {

                return DatabaseStep(definition.getValue())
            }
            DockerInstallationStepType.STACK.type -> {

                return Stack(definition.getValue())
            }
            DockerInstallationStepType.BUILD.type -> {

                return Build(definition.getValue())
            }
        }
        throw IllegalArgumentException("Unknown installation step type: ${definition.type}")
    }
}