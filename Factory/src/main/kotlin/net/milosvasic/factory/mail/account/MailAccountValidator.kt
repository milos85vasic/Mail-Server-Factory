package net.milosvasic.factory.mail.account

import net.milosvasic.factory.common.Validation
import net.milosvasic.factory.validation.Validator

class MailAccountValidator(private val credentialsStrength: Int = 2) : Validation<MailAccount> {

    @Throws(IllegalArgumentException::class)
    override fun validate(vararg what: MailAccount): Boolean {

        Validator.Arguments.validateSingle(what)
        val account = what[0]
        if (validateCredentials(account.credentials)) {
            val regex = getRegex("^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$")
            if (!account.name.matches(regex)) {

                throw IllegalArgumentException("Invalid email address: ${account.name}")
            }
            account.getAliases().forEach {

                if (!it.matches(regex)) {

                    throw IllegalArgumentException("Invalid email alias: ${account.name}")
                }
            }
        } else {

            val name = account.name
            val credentials = account.credentials
            throw IllegalArgumentException("Provided credentials for account '$name' are too weak: $credentials")
        }
        return true
    }

    private fun validateCredentials(credentials: String, strength: Int = credentialsStrength): Boolean {

        val weak = "^(?=.*[a-z])(?=.*[0-9])(?=.{8,})"
        val medium = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.{8,})"
        val strong = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#\$%\\^&\\*])(?=.{8,})"

        val parameters = mutableListOf<String>()
        when {
            strength == 1 -> {
                parameters.add(weak)
            }
            strength == 2 -> {
                parameters.add(medium)
            }
            strength >= 3 -> {
                parameters.add(medium)
                parameters.add(strong)
            }
        }
        parameters.forEach {
            val regex = getRegex(it)
            if (!credentials.matches(regex)) {

                return false
            }
        }
        return true
    }

    private fun getRegex(what: String) = Regex(what)
}