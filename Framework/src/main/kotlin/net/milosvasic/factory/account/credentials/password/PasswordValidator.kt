package net.milosvasic.factory.account.credentials.password

import net.milosvasic.factory.common.Validation
import net.milosvasic.factory.validation.Validator

class PasswordValidator(private val strength: PasswordStrength) : Validation<Password> {

    @Throws(IllegalArgumentException::class)
    override fun validate(vararg what: Password): Boolean {

        Validator.Arguments.validateSingle(what)
        val password = what[0].value

        val weak = "^(?=.*[a-z])(?=.*[0-9])(?=.{8,})"
        val medium = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.{8,})"
        val strong = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#\$%\\^&\\*])(?=.{8,})"

        val parameters = mutableListOf<String>()
        when {
            strength.value == 1 -> {
                parameters.add(weak)
            }
            strength.value == 2 -> {
                parameters.add(medium)
            }
            strength.value >= 3 -> {
                parameters.add(medium)
                parameters.add(strong)
            }
        }
        parameters.forEach {
            val regex = getRegex(it)
            if (!password.matches(regex)) {

                return false
            }
        }
        return true
    }

    private fun getRegex(what: String) = Regex(what)
}