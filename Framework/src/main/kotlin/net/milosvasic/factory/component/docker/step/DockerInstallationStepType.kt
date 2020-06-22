package net.milosvasic.factory.component.docker.step

import net.milosvasic.factory.component.installer.step.InstallationStepType

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