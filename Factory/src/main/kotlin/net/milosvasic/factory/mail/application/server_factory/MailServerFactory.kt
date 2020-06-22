package net.milosvasic.factory.mail.application.server_factory

import net.milosvasic.factory.application.server_factory.ServerFactory
import net.milosvasic.factory.configuration.ConfigurationFactory
import net.milosvasic.factory.mail.configuration.MailServerConfigurationFactory

class MailServerFactory(arguments: List<String> = listOf()) : ServerFactory(arguments) {

    override fun getConfigurationFactory(): ConfigurationFactory<*> {

        return MailServerConfigurationFactory()
    }
}