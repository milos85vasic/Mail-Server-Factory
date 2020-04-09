package net.milosvasic.factory.mail.configuration

class InstallationStepDefinition(
    val type: String,
    private val value: String
) {

    fun getValue(): String {

        // TODO: Apply variables
        return value
    }

    override fun toString(): String {
        return "InstallationStepDefinition(type='$type', value='${getValue()}')"
    }
}