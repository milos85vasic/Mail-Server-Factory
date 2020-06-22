package net.milosvasic.factory.configuration

import java.util.concurrent.LinkedBlockingQueue

open class ConfigurationInclude(

        var includes: LinkedBlockingQueue<String>?,
        var software: LinkedBlockingQueue<String>?,
        var containers: LinkedBlockingQueue<String>?,
        var variables: VariableNode? = null
) {

    override fun toString(): String {
        return "ConfigurationInclude(\nincludes=$includes, \nvariables=$variables, \nsoftware=$software, \ncontainers=$containers\n)"
    }
}