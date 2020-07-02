package net.milosvasic.factory.mail.configuration.variable

import net.milosvasic.factory.configuration.variable.Context

object Context {

    val ServiceMailReceive = object : Context {
        override fun context() = "MAIL_RECEIVE"
    }
}