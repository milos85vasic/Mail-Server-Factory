package net.milosvasic.factory.mail.component.installer.step

enum class InstallationStepType(
    val type: String
) {

    UNKNOWN("unknown"),
    PACKAGE_GROUP("packageGroup"),
    PACKAGES("packages"),
    COMMAND("command"),
    REBOOT("reboot"),
    COPY("copy"),
    CONDITION("condition");

    companion object {

        fun getByValue(value: String): InstallationStepType {
            InstallationStepType.values().forEach {
                if (it.type == value) {
                    return it
                }
            }
            return UNKNOWN
        }
    }
}