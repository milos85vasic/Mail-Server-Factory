package net.milosvasic.factory.configuration

data class SoftwareConfigurationItem(
    val name: String,
    val version: String,
    val installationSteps: Map<String, List<InstallationStepDefinition>>
)