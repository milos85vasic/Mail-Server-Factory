package net.milosvasic.factory.mail.configuration.variable

import net.milosvasic.factory.configuration.variable.Key

object Key {

    val DbDirectory = object : Key {
        override fun key() = "DB_DIRECTORY"
    }

    val TableDomains = object : Key {
        override fun key() = "TABLE_DOMAINS"
    }

    val TableUsers = object : Key {
        override fun key() = "TABLE_USERS"
    }

    val TableAliases = object : Key {
        override fun key() = "TABLE_ALIASES"
    }

    val ViewDomains = object : Key {
        override fun key() = "VIEW_DOMAINS"
    }

    val ViewUsers = object : Key {
        override fun key() = "VIEW_USERS"
    }

    val ViewAliases = object : Key {
        override fun key() = "VIEW_ALIASES"
    }
}