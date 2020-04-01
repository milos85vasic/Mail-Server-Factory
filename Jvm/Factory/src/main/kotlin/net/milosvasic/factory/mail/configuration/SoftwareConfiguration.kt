package net.milosvasic.factory.mail.configuration

import com.google.gson.Gson
import com.google.gson.JsonParseException
import net.milosvasic.factory.mail.common.ObtainParametrized
import java.io.File

data class SoftwareConfiguration(
    var configuration: String,
    val software: List<SoftwareConfigurationItem> = listOf(

        // TODO: Provide with defaults.
    )
) {

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

                        val msg = "Software configuration could not be obtained from: ${configurationFile.absolutePath}"
                        throw IllegalArgumentException(msg)
                    }
                }
            }
        }
    }
}