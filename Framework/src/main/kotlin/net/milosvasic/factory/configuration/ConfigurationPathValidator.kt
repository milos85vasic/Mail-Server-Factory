package net.milosvasic.factory.configuration

import net.milosvasic.factory.common.Validation
import net.milosvasic.factory.validation.Validator
import java.io.File

class ConfigurationPathValidator : Validation<String> {

    @Throws(IllegalArgumentException::class)
    override fun validate(vararg what: String): Boolean {

        Validator.Arguments.validateSingle(what)
        val path = what[0]
        if (path.isEmpty()) {
            throw IllegalArgumentException("Path is empty")
        }
        val file = File(path)
        if (!file.exists()) {
            throw IllegalArgumentException("Path does not exist: ${file.absolutePath}")
        }
        return true
    }
}