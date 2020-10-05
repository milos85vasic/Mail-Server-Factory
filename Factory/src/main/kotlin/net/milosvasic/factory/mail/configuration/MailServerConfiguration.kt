package net.milosvasic.factory.mail.configuration

import net.milosvasic.factory.EMPTY
import net.milosvasic.factory.configuration.Configuration
import net.milosvasic.factory.configuration.SoftwareConfiguration
import net.milosvasic.factory.configuration.definition.Definition
import net.milosvasic.factory.configuration.variable.Node
import net.milosvasic.factory.mail.account.MailAccount
import net.milosvasic.factory.remote.Remote
import java.util.concurrent.LinkedBlockingQueue

class MailServerConfiguration(

        name: String = String.EMPTY,
        remote: Remote,
        uses: LinkedBlockingQueue<Definition>?,
        includes: LinkedBlockingQueue<String>?,
        software: LinkedBlockingQueue<String>?,
        containers: LinkedBlockingQueue<String>?,
        variables: Node? = null,
        overrides: MutableMap<String, MutableMap<String, SoftwareConfiguration>>?,
        enabled: Boolean? = null,
        var accounts: LinkedBlockingQueue<MailAccount>?

) : Configuration(

        name, remote, uses, includes, software, containers, variables, overrides, enabled
) {

    @Throws(IllegalArgumentException::class)
    override fun merge(configuration: Configuration) {
        super.merge(configuration)

        if (configuration is MailServerConfiguration) {
            configuration.accounts?.let {
                accounts?.addAll(it)
            }
        }
    }
}