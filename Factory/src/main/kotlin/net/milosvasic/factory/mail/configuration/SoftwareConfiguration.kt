package net.milosvasic.factory.mail.configuration

import com.google.gson.Gson
import com.google.gson.JsonParseException
import net.milosvasic.factory.mail.EMPTY
import net.milosvasic.factory.mail.common.ObtainParametrized
import net.milosvasic.factory.mail.component.installer.step.InstallationStep
import net.milosvasic.factory.mail.component.installer.step.InstallationStepFactory
import net.milosvasic.factory.mail.validation.Validator
import java.io.File

data class SoftwareConfiguration(
        var configuration: String = String.EMPTY,
        val software: MutableList<SoftwareConfigurationItem> = mutableListOf(),
        val includes: MutableList<String> = mutableListOf()
) : ObtainParametrized<String, Map<String, List<InstallationStep<*>>>> {

    companion object : ObtainParametrized<String, SoftwareConfiguration> {

        @Throws(IllegalArgumentException::class, JsonParseException::class)
        override fun obtain(vararg param: String): SoftwareConfiguration {

            Validator.Arguments.validateSingle(param)
            val configurationName = param[0]
            val configurationFile = File(configurationName)
            if (configurationFile.exists()) {

                val json = configurationFile.readText()
                val gson = Gson()
                val instance = gson.fromJson(json, SoftwareConfiguration::class.java)
                instance.configuration = configurationName
                val included = mutableListOf<SoftwareConfiguration>()
                instance.includes.forEach { include ->
                    included.add(obtain(include))
                }
                included.forEach { config ->
                    instance.merge(config)
                }
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
        val factory = InstallationStepFactory()
        val installationSteps = mutableMapOf<String, List<InstallationStep<*>>>()
        software.forEach {
            val os = param[0]
            val msg = "No installation steps for '${it.name}' installation step for $os"
            val recipe = it.installationSteps[os] ?: throw IllegalStateException(msg)
            val items = mutableListOf<InstallationStep<*>>()
            recipe.forEach { definition ->
                items.add(factory.obtain(definition))
            }
            installationSteps[it.name] = items
        }
        return installationSteps
    }

    fun merge(configuration: SoftwareConfiguration) {

        software.addAll(configuration.software)
        includes.addAll(configuration.includes)
    }
}