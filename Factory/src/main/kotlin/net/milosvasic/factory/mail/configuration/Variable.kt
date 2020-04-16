package net.milosvasic.factory.mail.configuration

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
                val rawVariable = ConfigurationManager.getConfiguration().variables[match].toString()
                val variable = parse(rawVariable)
                result = result.replace("$open$match$close", variable)
            }
        }
        return result
    }
}