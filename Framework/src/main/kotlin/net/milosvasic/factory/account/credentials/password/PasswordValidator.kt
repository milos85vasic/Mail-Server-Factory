package net.milosvasic.factory.account.credentials.password

import net.milosvasic.factory.common.Validation
import net.milosvasic.factory.validation.Validator
import java.util.regex.Pattern

class PasswordValidator(private val strength: PasswordStrength) : Validation<Password> {

    @Throws(IllegalArgumentException::class)
    override fun validate(vararg what: Password): Boolean {

        Validator.Arguments.validateSingle(what)
        val password = what[0].value

        val digits = "(?=.*[0-9])"
        val noWhiteSpace = "(?=\\S+$)"
        val passwordLengthLong = "{10,16}"
        val passwordLengthMedium = "{8,16}"
        val lowercaseLetters = "(?=.*[a-z])"
        val uppercaseLetters = "(?=.*[A-Z])"
        val specialCharacters = "(?=.*[@#$%])"

        val weak = StringBuilder("(")
                .append(digits)
                .append(lowercaseLetters)
                .append(noWhiteSpace)
                .append(".")
                .append(passwordLengthMedium)
                .append(")")
                .toString()

        val medium = StringBuilder("(")
                .append(digits)
                .append(lowercaseLetters)
                .append(noWhiteSpace)
                .append(uppercaseLetters)
                .append(".")
                .append(passwordLengthMedium)
                .append(")")
                .toString()

        val strong = StringBuilder("(")
                .append(digits)
                .append(lowercaseLetters)
                .append(noWhiteSpace)
                .append(uppercaseLetters)
                .append(specialCharacters)
                .append(".")
                .append(passwordLengthLong)
                .append(")")
                .toString()

        val parameters = mutableListOf<String>()
        when {
            strength.value == PasswordStrength.WEAK.value -> {
                parameters.add(weak)
            }
            strength.value == PasswordStrength.MEDIUM.value -> {
                parameters.add(medium)
            }
            strength.value >= PasswordStrength.STRONG.value -> {
                parameters.add(medium)
                parameters.add(strong)
            }
        }
        parameters.forEach {
            val pattern = Pattern.compile(it)
            val matcher = pattern.matcher(password)
            return matcher.matches()
        }
        return true
    }
}