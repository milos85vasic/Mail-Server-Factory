package net.milosvasic.factory.mail.configuration

import com.google.gson.reflect.TypeToken
import net.milosvasic.factory.configuration.ConfigurationFactory
import java.lang.reflect.Type

class MailServerConfigurationFactory : ConfigurationFactory<MailServerConfiguration>() {

    override fun getType(): Type {

        return object : TypeToken<MailServerConfiguration>() {}.type
    }

    override fun onInstantiated(configuration: MailServerConfiguration) {

        // TODO:
    }
}