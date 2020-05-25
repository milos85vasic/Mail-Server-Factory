package net.milosvasic.factory.mail.component.installer.step

enum class InstallationStepType(val type: String) {

    UNKNOWN("unknown"),
    PACKAGE_GROUP("packageGroup"),
    PACKAGES("packages"),
    COMMAND("command"),
    REBOOT("reboot"),
    DEPLOY("deploy"),
    DATABASE("database"),
    CONDITION("condition"),
    SKIP_CONDITION("skipCondition"),
    CHECK("check"),
    CONDITION_CHECK("conditionCheck")
}