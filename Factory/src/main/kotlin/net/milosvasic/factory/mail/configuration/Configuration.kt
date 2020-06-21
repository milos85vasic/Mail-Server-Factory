package net.milosvasic.factory.mail.configuration

import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import net.milosvasic.factory.mail.EMPTY
import net.milosvasic.factory.mail.common.obtain.ObtainParametrized
import net.milosvasic.factory.mail.log
import net.milosvasic.factory.mail.remote.Remote
import net.milosvasic.factory.mail.validation.Validator
import java.io.File
import java.util.concurrent.LinkedBlockingQueue

class Configuration(
        val name: String = String.EMPTY,
        val remote: Remote,

        includes: LinkedBlockingQueue<String>?,
        software: LinkedBlockingQueue<String>?,
        containers: LinkedBlockingQueue<String>?,
        variables: VariableNode? = null

) : ConfigurationInclude(

        includes,
        software,
        containers,
        variables
) {

    companion object : ObtainParametrized<File, Configuration> {

        fun getConfigurationFilePath(path: String): String {

            var fullPath = path
            val defaultConfigurationFile = "Definition.json"
            if (!path.endsWith(".json")) {
                fullPath += "${File.separator}$defaultConfigurationFile"
            }
            return fullPath
        }

        @Throws(IllegalArgumentException::class)
        override fun obtain(vararg param: File): Configuration {

            Validator.Arguments.validateSingle(param)
            val configurationFile = param[0]
            if (configurationFile.exists()) {

                log.v("Configuration file: ${configurationFile.absolutePath}")
                val configurationJson = configurationFile.readText()
                val variablesDeserializer = VariableNode.getDeserializer()
                val gsonBuilder = GsonBuilder()
                gsonBuilder.registerTypeAdapter(VariableNode::class.java, variablesDeserializer)
                val gson = gsonBuilder.create()
                try {
                    val configuration = gson.fromJson(configurationJson, Configuration::class.java)
                    val iterator = configuration.includes?.iterator()
                    iterator?.let {
                        while (it.hasNext()){
                            val include = it.next()
                            val includeFile = File(include)
                            val includedConfiguration = obtain(includeFile)
                            configuration.merge(includedConfiguration)
                        }
                    }
                    return configuration

                } catch (e: JsonParseException) {

                    throw IllegalArgumentException("Unable to parse JSON: ${e.message}")
                } catch (e: IllegalArgumentException) {

                    throw IllegalArgumentException("Unable to parse JSON: ${e.message}")
                }
            } else {

                throw IllegalArgumentException("File does not exist: ${configurationFile.absoluteFile}")
            }
        }
    }

    fun merge(configuration: ConfigurationInclude) {

        configuration.includes?.let {
            includes?.addAll(it)
        }
        configuration.variables?.let {
            variables?.append(it)
        }
        configuration.software?.let {
            software?.addAll(it)
        }
        configuration.containers?.let {
            containers?.addAll(it)
        }
    }

    fun mergeVariables(variables: VariableNode?) {
        variables?.let { toAppend ->
            if (this.variables == null) {
                this.variables = toAppend
            } else {
                toAppend.children.forEach { child ->
                    this.variables?.append(child)
                }
            }
        }
    }

    @Throws(IllegalStateException::class)
    fun getVariableParsed(key: String): Any? {

        variables?.let { it ->
            val value = it.get(key)
            if (value is String) {
                return Variable.parse(value)
            }
            return value
        }
        return null
    }

    override fun toString(): String {
        return "Configuration(\nname='$name', \nremote=$remote\n)\n${super.toString()}"
    }
}