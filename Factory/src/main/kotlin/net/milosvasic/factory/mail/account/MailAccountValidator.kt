package net.milosvasic.factory.mail.account

import net.milosvasic.factory.common.Validation
import net.milosvasic.factory.validation.Validator

class MailAccountValidator : Validation<MailAccount> {

    @Throws(IllegalArgumentException::class)
    override fun validate(vararg what: MailAccount): Boolean {

        Validator.Arguments.validateSingle(what)
        val account = what[0]
        if (account.getCredentials().validate()) {
            val regex = Regex("^[\\w-_.+]*[\\w-_.]@([\\w]+\\.)+[\\w]+[\\w]$")
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
            val credentials = account.getCredentials().value
            throw IllegalArgumentException("Provided credentials for account '$name' are too weak: $credentials")
        }
        return true
    }
}