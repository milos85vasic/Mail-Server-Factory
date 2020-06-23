package net.milosvasic.factory.configuration

import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import net.milosvasic.factory.common.obtain.ObtainParametrized
import net.milosvasic.factory.log
import net.milosvasic.factory.validation.Validator
import java.io.File
import java.lang.reflect.Type
import java.util.concurrent.LinkedBlockingQueue

abstract class ConfigurationFactory<T : Configuration> : ObtainParametrized<File, T> {

    abstract fun getType(): Type

    abstract fun onInstantiated(configuration: T)

    abstract fun validateConfiguration(configuration: T): Boolean

    @Throws(IllegalArgumentException::class)
    override fun obtain(vararg param: File): T {

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

                val configuration: T = gson.fromJson(configurationJson, getType())
                postInstantiate(configuration)
                val iterator = configuration.includes?.iterator()
                iterator?.let {
                    while (it.hasNext()) {
                        val include = it.next()
                        val includeFile = File(include)
                        val includedConfiguration = obtain(includeFile)
                        configuration.merge(includedConfiguration)
                    }
                }
                if (validateConfiguration(configuration)) {
                    return configuration
                }

            } catch (e: JsonParseException) {

                log.e(e)
                throw IllegalArgumentException("Unable to parse JSON: ${e.message}")
            } catch (e: JsonSyntaxException) {

                log.e(e)
                throw IllegalArgumentException("Unable to parse JSON: ${e.message}")
            }
        } else {

            throw IllegalArgumentException("File does not exist: ${configurationFile.absoluteFile}")
        }
        throw IllegalArgumentException("Could not obtain configuration")
    }

    private fun postInstantiate(configuration: T) {

        if (configuration.includes == null) {
            configuration.includes = LinkedBlockingQueue()
        }
        if (configuration.software == null) {
            configuration.software = LinkedBlockingQueue()
        }
        if (configuration.containers == null) {
            configuration.containers = LinkedBlockingQueue()
        }
        if (configuration.variables == null) {
            configuration.variables = VariableNode()
        }
        onInstantiated(configuration)
    }
}