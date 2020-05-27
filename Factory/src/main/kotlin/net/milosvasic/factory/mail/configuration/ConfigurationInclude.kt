package net.milosvasic.factory.mail.configuration

open class ConfigurationInclude(

        val includes: MutableList<String>?,
        val software: MutableList<String>?,
        val containers: MutableList<String>?,
        val variables: VariableNode = VariableNode()
) {

    override fun toString(): String {
        return "ConfigurationInclude(\nincludes=$includes, \nvariables=$variables, \nsoftware=$software, \ncontainers=$containers\n)"
    }
}