package net.milosvasic.factory.mail.configuration

import net.milosvasic.factory.mail.service.Service

data class Configuration(
    val name: String,
    val services: List<Service>
)