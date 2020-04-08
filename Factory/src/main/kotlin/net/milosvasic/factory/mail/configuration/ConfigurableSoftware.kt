package net.milosvasic.factory.mail.configuration

interface ConfigurableSoftware {

    fun clearConfiguration()

    fun setConfiguration(configuration: SoftwareConfiguration)
}