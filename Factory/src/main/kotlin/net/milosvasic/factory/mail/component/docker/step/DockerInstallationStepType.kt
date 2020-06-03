package net.milosvasic.factory.mail.component.docker.step

import net.milosvasic.factory.mail.component.installer.step.InstallationStepType

enum class DockerInstallationStepType(
        val type: String
) {

    BUILD("build"),
    STACK("stack"),
    NETWORK("network"),
    UNKNOWN(InstallationStepType.UNKNOWN.type);

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