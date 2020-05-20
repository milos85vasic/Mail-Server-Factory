package net.milosvasic.factory.mail.component.docker.step

import net.milosvasic.factory.mail.component.installer.step.InstallationStepType

enum class DockerInstallationStepType(
        val type: String
) {

    UNKNOWN(InstallationStepType.UNKNOWN.type),
    STACK("stack");

    companion object {

        fun getByValue(value: String): DockerInstallationStepType {
            values().forEach {
                if (it.type == value) {
                    return it
                }
            }
            return UNKNOWN
        }
    }
}