package net.milosvasic.factory.mail.configuration

import net.milosvasic.factory.mail.remote.Remote

data class Configuration(
    val name: String,
    val remote: Remote,
    val software: List<String>
)