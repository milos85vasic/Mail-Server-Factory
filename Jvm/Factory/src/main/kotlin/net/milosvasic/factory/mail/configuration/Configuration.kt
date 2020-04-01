package net.milosvasic.factory.mail.configuration

import net.milosvasic.factory.mail.remote.Remote
import net.milosvasic.factory.mail.service.Definition

data class Configuration(
    val name: String,
    val services: List<Definition>,
    val remote: Remote,
    val softwareConfiguration: String = "default"
)