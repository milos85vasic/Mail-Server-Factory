package net.milosvasic.factory.configuration

import net.milosvasic.factory.EMPTY
import java.util.regex.Pattern

object Variable {

    const val open = "{{"
    const val close = "}}"

    private fun getRegex() = "${Pattern.quote(open)}(.*?)${Pattern.quote(close)}"

    private fun getPattern() = Pattern.compile(getRegex())

    @Throws(IllegalStateException::class)
    fun parse(value: String): String {

        val pattern = getPattern()
        var result = value
        val matcher = pattern.matcher(result)
        while (matcher.find()) {
            val match = matcher.group(1)
            if (match.isNotEmpty()) {

                @Throws(IllegalStateException::class)
                fun noVariable(match: String) {
                    throw IllegalStateException("No variable defined in the configuration for: '$match'")
                }

                val variables = ConfigurationManager.getConfiguration().variables
                if (variables == null) {
                    noVariable(match)
                } else {
                    val rawVariable = variables.get(match) ?: noVariable(match)
                    val variable = parse(rawVariable.toString())
                    if (variable == String.EMPTY) {
                        noVariable(match)
                    }
                    result = result.replace("$open$match$close", variable)
                }
            }
        }
        return result
    }
}