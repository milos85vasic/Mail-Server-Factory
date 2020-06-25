package net.milosvasic.factory.mail.application.server_factory

import net.milosvasic.factory.application.server_factory.ServerFactory
import net.milosvasic.factory.execution.flow.FlowBuilder
import net.milosvasic.factory.execution.flow.callback.TerminationCallback
import net.milosvasic.factory.log
import net.milosvasic.factory.mail.BuildInfo
import net.milosvasic.factory.mail.configuration.MailServerConfiguration
import net.milosvasic.factory.mail.configuration.MailServerConfigurationFactory
import net.milosvasic.factory.mail.manager.MailFactory
import net.milosvasic.factory.remote.Connection

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

    @Throws(IllegalStateException::class, IllegalArgumentException::class)
    override fun getTerminationFlow(connection: Connection): FlowBuilder<*, *, *> {

        val mailFactory = MailFactory(connection)
        return mailFactory
                .getMailCreationFlow()
                .onFinish(TerminationCallback(this))
    }

    override fun getConfigurationFactory() = MailServerConfigurationFactory()

    override fun getLogTag() = BuildInfo.NAME.replace("-", " ")
}