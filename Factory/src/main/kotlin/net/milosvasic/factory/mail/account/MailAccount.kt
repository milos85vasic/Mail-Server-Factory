package net.milosvasic.factory.mail.account

import net.milosvasic.factory.account.Account

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

    override fun toString(): String {
        return "MailAccount(${printAccount()}, aliases=$aliases)"
    }
}