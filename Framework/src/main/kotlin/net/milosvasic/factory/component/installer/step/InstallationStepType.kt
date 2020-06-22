package net.milosvasic.factory.component.installer.step

enum class InstallationStepType(val type: String) {

    UNKNOWN("unknown"),
    PACKAGE_GROUP("packageGroup"),
    PACKAGES("packages"),
    COMMAND("command"),
    REBOOT("reboot"),
    DEPLOY("deploy"),
    DATABASE("database"),
    CONDITION("condition"),
    PORT_CHECK("portCheck"),
    PORT_REQUIRED("portRequired"),
    CERTIFICATE("certificate"),
    TLS_CERTIFICATE("tlsCertificate"),
    SKIP_CONDITION("skipCondition"),
    CHECK("check"),
    CONDITION_CHECK("conditionCheck")
}