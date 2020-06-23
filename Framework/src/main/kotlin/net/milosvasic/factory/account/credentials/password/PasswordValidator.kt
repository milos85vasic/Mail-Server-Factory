package net.milosvasic.factory.account.credentials.password

import net.milosvasic.factory.common.Validation
import net.milosvasic.factory.validation.Validator
import java.lang.StringBuilder
import java.util.regex.Pattern

class PasswordValidator(private val strength: PasswordStrength) : Validation<Password> {

    @Throws(IllegalArgumentException::class)
    override fun validate(vararg what: Password): Boolean {

        Validator.Arguments.validateSingle(what)
        val password = what[0].value

        val `a digit must occur at least once` = "(?=.*[0-9])"
        val `a lower case letter must occur at least once` = "(?=.*[a-z])"
        val `an upper case letter must occur at least once` = "(?=.*[A-Z])"
        val `a special character must occur at least once` = "(?=.*[@#$%])"
        val `no whitespace allowed in the entire string` = "(?=\\S+$)"
        val `length of password from minimum 8 letters to maximum 16 letters` = "{8,16}"

        val weak = StringBuilder("(")
                .append(`a digit must occur at least once`)
                .append(`a lower case letter must occur at least once`)
                .append(`no whitespace allowed in the entire string`)
                .append(".")
                .append(`length of password from minimum 8 letters to maximum 16 letters`)
                .append(")")
                .toString()

        val medium = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.{8,})$"
        val strong = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#\$%\\^&\\*])(?=.{8,})$"

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