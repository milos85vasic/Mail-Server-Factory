package net.milosvasic.factory.mail.configuration

import java.util.regex.Pattern

class InstallationStepDefinition(
    val type: String,
    private val value: String
) {

    @Throws(IllegalStateException::class)
    fun getValue(): String {

        val variableOpening = "{{"
        val variableClosing = "}}"
        val regex = "${Pattern.quote(variableOpening)}(.*?)${Pattern.quote(variableClosing)}"
        val pattern = Pattern.compile(regex)
        var result = value
        val matcher = pattern.matcher(result)
        while (matcher.find()) {
            val match = matcher.group(1)
            if (match.isNotEmpty()) {
                val variable = ConfigurationManager.getConfiguration().variables[match]
                result = result.replace("$variableOpening$match$variableClosing", variable.toString())
            }
        }
        return result
    }
}