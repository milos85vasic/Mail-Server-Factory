package net.milosvasic.factory.mail.configuration

import com.google.gson.Gson
import com.google.gson.JsonParseException
import net.milosvasic.factory.mail.common.ObtainParametrized
import net.milosvasic.factory.mail.error.ERROR
import net.milosvasic.factory.mail.log
import net.milosvasic.factory.mail.remote.Remote
import net.milosvasic.factory.mail.validation.Validator
import java.io.File

data class Configuration(
        val name: String,
        val remote: Remote,
        val variables: Map<String, Any>,
        val software: List<String>,
        val containers: List<String>
) {

    companion object : ObtainParametrized<File, Configuration> {

        @Throws(IllegalArgumentException::class)
        override fun obtain(vararg param: File): Configuration {

            Validator.Arguments.validateSingle(param)
            val configurationFile = param[0]
            if (configurationFile.exists()) {

                log.v("Configuration file: ${configurationFile.absolutePath}")
                val configurationJson = configurationFile.readText()
                val gson = Gson()
                try {
                    return gson.fromJson(configurationJson, Configuration::class.java)
                    
                } catch (e: JsonParseException) {

                    throw IllegalArgumentException("Unable to parse JSON: ${e.message}")
                } catch (e: IllegalArgumentException) {

                    throw IllegalArgumentException("Unable to parse JSON: ${e.message}")
                }
            } else {

                val msg = ERROR.FILE_DOES_NOT_EXIST.message
                throw IllegalArgumentException("$msg: ${configurationFile.absoluteFile}")
            }
        }
    }

    @Throws(IllegalStateException::class)
    fun getVariableParsed(key: String): Any? {
        val variable = variables[key]
        variable?.let {
            val str = it.toString()
            if (str.contains(Variable.open) && str.contains(Variable.close)) {
                return Variable.parse(str)
            }
        }
        return variable
    }
}