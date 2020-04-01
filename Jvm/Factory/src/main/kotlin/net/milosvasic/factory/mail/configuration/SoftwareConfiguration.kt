package net.milosvasic.factory.mail.configuration

import com.google.gson.Gson
import com.google.gson.JsonParseException
import net.milosvasic.factory.mail.common.ObtainParametrized
import net.milosvasic.factory.mail.component.installer.step.InstallationStep
import net.milosvasic.factory.mail.component.installer.step.InstallationStepFactory
import java.io.File

data class SoftwareConfiguration(
    var configuration: String,
    val software: List<SoftwareConfigurationItem> = listOf(

        // TODO: Provide with defaults.
    )
) : ObtainParametrized<String, List<InstallationStep>> {

    companion object : ObtainParametrized<String, SoftwareConfiguration> {

        private const val DEFAULT = "default"

        @Throws(IllegalArgumentException::class, JsonParseException::class)
        override fun obtain(vararg param: String): SoftwareConfiguration {

            if (param.size > 1 || param.isEmpty()) {
                throw IllegalArgumentException("Expected 1 argument")
            }
            return when (val configurationName = param[0]) {
                DEFAULT -> {
                    SoftwareConfiguration(configurationName)
                }
                else -> {
                    val configurationFile = File(configurationName)
                    if (configurationFile.exists()) {

                        val json = configurationFile.readText()
                        val gson = Gson()
                        val instance = gson.fromJson(json, SoftwareConfiguration::class.java)
                        instance.configuration = configurationName
                        instance
                    } else {

                        val msg = "Software configuration file does not exist: ${configurationFile.absolutePath}"
                        throw IllegalArgumentException(msg)
                    }
                }
            }
        }
    }

    @Synchronized
    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    override fun obtain(vararg param: String): List<InstallationStep> {

        if (param.size > 1 || param.isEmpty()) {
            throw IllegalArgumentException("Expected 1 argument")
        }
        val factory = InstallationStepFactory()
        val installationSteps = mutableListOf<InstallationStep>()
        software.forEach {
            val os = param[0]
            val msg = "No installation steps for ${it.name} for $os"
            val recipe = it.installationSteps[os] ?: throw IllegalStateException(msg)
            recipe.forEach { definition ->
                installationSteps.add(factory.obtain(definition))
            }
        }
        return installationSteps
    }
}