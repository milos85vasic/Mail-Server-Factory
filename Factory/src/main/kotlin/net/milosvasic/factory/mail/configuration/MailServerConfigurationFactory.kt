package net.milosvasic.factory.mail.configuration

import com.google.gson.reflect.TypeToken
import net.milosvasic.factory.configuration.ConfigurationFactory
import net.milosvasic.factory.log
import net.milosvasic.factory.mail.account.MailAccountValidator
import java.lang.reflect.Type
import java.util.concurrent.LinkedBlockingQueue

class MailServerConfigurationFactory : ConfigurationFactory<MailServerConfiguration>() {

    override fun getType(): Type {

        return object : TypeToken<MailServerConfiguration>() {}.type
    }

    override fun onInstantiated(configuration: MailServerConfiguration) {

        if (configuration.accounts == null) {
            configuration.accounts = LinkedBlockingQueue()
        }
    }

    override fun validateConfiguration(configuration: MailServerConfiguration): Boolean {

        val validator = MailAccountValidator()
        configuration.accounts?.forEach { account ->

            try {
                if (!validator.validate(account)) {

                    log.e("Account is not valid: $account")
                    return false
                }
            } catch (e: IllegalArgumentException) {

                log.e(e)
                return false
            }
        }
        return true
    }
}