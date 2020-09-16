package net.milosvasic.factory.mail.account

class PostmasterAccount(

        name: String,
        credentials: String,
        type: String,
        aliases: MutableList<String>?

) : MailAccount(name, credentials, type, aliases)