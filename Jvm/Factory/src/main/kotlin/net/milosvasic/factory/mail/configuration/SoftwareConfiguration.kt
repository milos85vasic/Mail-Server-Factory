package net.milosvasic.factory.mail.configuration

import com.google.gson.Gson
import com.google.gson.JsonParseException
import net.milosvasic.factory.mail.common.Obtain
import net.milosvasic.factory.mail.common.ObtainParametrized
import net.milosvasic.factory.mail.component.installer.InstallationStep
import net.milosvasic.factory.mail.log
import java.io.File

data class SoftwareConfiguration(
    var configuration: String,
    val software: List<SoftwareConfigurationItem> = listOf(

        // TODO: Provide with defaults.
    )
) : Obtain<List<InstallationStep>> {

    companion object : ObtainParametrized<String, SoftwareConfiguration> {

        private const val DEFAULT = "default"

        @Throws(IllegalArgumentException::class, JsonParseException::class)
        override fun obtain(vararg param: String): SoftwareConfiguration {

            if (param.size > 1 || param.isEmpty()) {
                throw IllegalStateException("Expected 1 argument")
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
    override fun obtain(): List<InstallationStep> {

        val installationSteps = mutableListOf<InstallationStep>()
        software.forEach {

            log.v("> > > $it")
        }

        return installationSteps
    }
}