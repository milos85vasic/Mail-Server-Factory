package net.milosvasic.factory.mail.configuration

import java.util.concurrent.LinkedBlockingQueue

open class ConfigurationInclude(

        val includes: LinkedBlockingQueue<String>?,
        val software: LinkedBlockingQueue<String>?,
        val containers: LinkedBlockingQueue<String>?,
        var variables: VariableNode? = null
) {

    override fun toString(): String {
        return "ConfigurationInclude(\nincludes=$includes, \nvariables=$variables, \nsoftware=$software, \ncontainers=$containers\n)"
    }
}