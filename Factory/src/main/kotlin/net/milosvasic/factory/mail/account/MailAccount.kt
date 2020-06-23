package net.milosvasic.factory.mail.account

import net.milosvasic.factory.account.Account
import net.milosvasic.factory.account.credentials.password.Password
import net.milosvasic.factory.account.credentials.password.PasswordStrength

class MailAccount(

        name: String,
        credentials: String,
        type: String,
        private var aliases: MutableList<String>?

) : Account(name, credentials, type) {

    fun getAliases(): MutableList<String> {

        if (aliases == null) {
            aliases = mutableListOf()
        }
        aliases?.let {
            return it
        }
        return mutableListOf()
    }

    override fun getCredentials() = Password(credentials, getPasswordStrength())

    override fun toString(): String {
        return "MailAccount(${printAccount()}, aliases=$aliases)"
    }

    private fun getPasswordStrength() : PasswordStrength {

        // TODO:
        return PasswordStrength.WEAK
    }
}