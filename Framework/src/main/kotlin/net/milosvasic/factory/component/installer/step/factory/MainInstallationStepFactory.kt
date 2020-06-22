package net.milosvasic.factory.component.installer.step.factory

import net.milosvasic.factory.component.docker.step.DockerInstallationStepType
import net.milosvasic.factory.component.docker.step.dockerfile.Build
import net.milosvasic.factory.component.docker.step.network.Network
import net.milosvasic.factory.component.docker.step.stack.Check
import net.milosvasic.factory.component.docker.step.stack.SkipConditionCheck
import net.milosvasic.factory.component.docker.step.stack.Stack
import net.milosvasic.factory.component.installer.step.CommandInstallationStep
import net.milosvasic.factory.component.installer.step.InstallationStep
import net.milosvasic.factory.component.installer.step.InstallationStepType
import net.milosvasic.factory.component.installer.step.PackageManagerInstallationStep
import net.milosvasic.factory.component.installer.step.certificate.Certificate
import net.milosvasic.factory.component.installer.step.certificate.TlsCertificate
import net.milosvasic.factory.component.installer.step.condition.Condition
import net.milosvasic.factory.component.installer.step.condition.SkipCondition
import net.milosvasic.factory.component.installer.step.database.DatabaseStep
import net.milosvasic.factory.component.installer.step.deploy.Deploy
import net.milosvasic.factory.component.installer.step.deploy.DeployValidator
import net.milosvasic.factory.component.installer.step.port.PortCheck
import net.milosvasic.factory.component.installer.step.port.PortCheckValidator
import net.milosvasic.factory.component.installer.step.reboot.Reboot
import net.milosvasic.factory.component.packaging.item.Group
import net.milosvasic.factory.component.packaging.item.Package
import net.milosvasic.factory.configuration.InstallationStepDefinition
import net.milosvasic.factory.terminal.command.RawTerminalCommand
import net.milosvasic.factory.validation.Validator

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
            InstallationStepType.CERTIFICATE.type -> {

                return Certificate(definition.getValue())
            }
            InstallationStepType.TLS_CERTIFICATE.type -> {

                return TlsCertificate(definition.getValue())
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
            InstallationStepType.PORT_REQUIRED.type -> {

                val arg = definition.getValue()
                return getPortCheck(arg, true)
            }
            InstallationStepType.PORT_CHECK.type -> {

                val arg = definition.getValue()
                return getPortCheck(arg, false)
            }
            InstallationStepType.DATABASE.type -> {

                return DatabaseStep(definition.getValue())
            }
            DockerInstallationStepType.STACK.type -> {

                return Stack(definition.getValue())
            }
            DockerInstallationStepType.NETWORK.type -> {

                return Network(definition.getValue())
            }
            DockerInstallationStepType.BUILD.type -> {

                return Build(definition.getValue())
            }
        }
        throw IllegalArgumentException("Unknown installation step type: ${definition.type}")
    }

    @Throws(IllegalArgumentException::class)
    private fun getPortCheck(arg: String, isPortAvailable: Boolean): PortCheck {

        val validator = PortCheckValidator()
        if (validator.validate(arg)) {

            val split = arg.split(PortCheck.delimiter)
            val ports = mutableListOf<Int>()
            split.forEach {
                val port = it.trim().toInt()
                ports.add(port)
            }
            return PortCheck(ports, isPortAvailable)
        } else {

            throw IllegalArgumentException("Invalid port check parameters")
        }
    }
}