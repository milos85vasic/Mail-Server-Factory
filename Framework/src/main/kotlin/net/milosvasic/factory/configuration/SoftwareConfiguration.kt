package net.milosvasic.factory.configuration

import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import net.milosvasic.factory.EMPTY
import net.milosvasic.factory.common.obtain.ObtainParametrized
import net.milosvasic.factory.component.installer.step.InstallationStep
import net.milosvasic.factory.component.installer.step.factory.InstallationStepFactories
import net.milosvasic.factory.log
import net.milosvasic.factory.validation.Validator
import java.io.File

data class SoftwareConfiguration(
        var configuration: String = String.EMPTY,
        var variables: VariableNode? = null,
        val software: MutableList<SoftwareConfigurationItem> = mutableListOf(),
        val includes: MutableList<String> = mutableListOf()
) : ObtainParametrized<String, Map<String, List<InstallationStep<*>>>> {

    companion object : ObtainParametrized<String, SoftwareConfiguration> {

        @Throws(IllegalArgumentException::class, JsonParseException::class)
        override fun obtain(vararg param: String): SoftwareConfiguration {

            Validator.Arguments.validateSingle(param)
            val configurationName = param[0]
            val configurationFile = File(configurationName)
            log.v("Configuration file: ${configurationFile.absolutePath}")
            if (configurationFile.exists()) {

                val json = configurationFile.readText()
                val gsonBuilder = GsonBuilder()
                val variablesDeserializer = VariableNode.getDeserializer()
                gsonBuilder.registerTypeAdapter(VariableNode::class.java, variablesDeserializer)
                val gson = gsonBuilder.create()

                val instance = gson.fromJson(json, SoftwareConfiguration::class.java)
                instance.configuration = configurationName
                val included = mutableListOf<SoftwareConfiguration>()
                instance.includes.forEach { include ->
                    included.add(obtain(include))
                }
                included.forEach { config ->
                    instance.merge(config)
                }
                // TODO: Handle vars.
                return instance
            } else {

                val msg = "Software configuration file does not exist: ${configurationFile.absolutePath}"
                throw IllegalArgumentException(msg)
            }
        }
    }

    @Synchronized
    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    override fun obtain(vararg param: String): Map<String, List<InstallationStep<*>>> {

        Validator.Arguments.validateSingle(param)
        val os = param[0]
        val factories = InstallationStepFactories
        val installationSteps = mutableMapOf<String, List<InstallationStep<*>>>()
        software.forEach {
            val steps = it.installationSteps[os]
            steps?.let { recipe ->
                val items = mutableListOf<InstallationStep<*>>()
                recipe.forEach { definition ->
                    items.add(factories.obtain(definition))
                }
                installationSteps[it.name] = items
            }
        }
        if (installationSteps.isEmpty()) {
            throw IllegalArgumentException("No installation steps for '$os' platform")
        }
        return installationSteps
    }

    fun merge(configuration: SoftwareConfiguration) {

        configuration.variables?.let { toAppend ->
            if (variables == null) {
                variables = toAppend
            } else {
                variables?.append(toAppend)
            }
        }
        software.addAll(configuration.software)
        includes.addAll(configuration.includes)
    }
}