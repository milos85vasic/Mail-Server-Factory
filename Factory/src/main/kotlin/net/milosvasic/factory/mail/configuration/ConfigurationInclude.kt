package net.milosvasic.factory.mail.configuration

open class ConfigurationInclude(

        val variables: MutableMap<String, MutableMap<String, Any>> = mutableMapOf(),
        val software: MutableList<String> = mutableListOf(),
        val containers: MutableList<String> = mutableListOf()
) {

    override fun toString(): String {
        return "ConfigurationInclude(variables=$variables, software=$software, containers=$containers)"
    }
}