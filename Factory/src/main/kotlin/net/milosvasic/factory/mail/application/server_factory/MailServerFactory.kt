package net.milosvasic.factory.mail.application.server_factory

import net.milosvasic.factory.application.server_factory.ServerFactory
import net.milosvasic.factory.configuration.ConfigurationFactory
import net.milosvasic.factory.log
import net.milosvasic.factory.mail.BuildInfo
import net.milosvasic.factory.mail.configuration.MailServerConfiguration
import net.milosvasic.factory.mail.configuration.MailServerConfigurationFactory

class MailServerFactory(arguments: List<String> = listOf()) : ServerFactory(arguments) {

    @Throws(IllegalStateException::class)
    override fun run() {
        configuration?.let {
            if (it is MailServerConfiguration) {
                it.accounts?.forEach { account ->

                    log.v("Mail account to be created: ${account.print()}")
                }
            } else {

                throw IllegalStateException("Unexpected configuration type: ${it::class.simpleName}")
            }
        }
        super.run()
    }

    override fun getConfigurationFactory() = MailServerConfigurationFactory()

    override fun getLogTag() = BuildInfo.NAME.replace("-", " ")
}