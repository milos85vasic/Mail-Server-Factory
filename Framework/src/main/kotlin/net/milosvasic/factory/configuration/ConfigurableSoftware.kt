package net.milosvasic.factory.configuration

interface ConfigurableSoftware {

    fun clearConfiguration()

    fun setConfiguration(configuration: SoftwareConfiguration)
}