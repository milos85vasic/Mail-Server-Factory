package net.milosvasic.factory.mail.configuration

data class SoftwareConfigurationItem(
    val name: String,
    val version: String,
    val installationSteps: Map<String, List<InstallationStepDefinition>>
)