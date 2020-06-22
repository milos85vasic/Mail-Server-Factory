package net.milosvasic.factory.test.implementation

import com.google.gson.reflect.TypeToken
import net.milosvasic.factory.configuration.ConfigurationFactory
import java.lang.reflect.Type

class StubServerConfigurationFactory : ConfigurationFactory<StubConfiguration>() {

    override fun getType(): Type {

        return object : TypeToken<StubConfiguration>() {}.type
    }

    override fun onInstantiated(configuration: StubConfiguration) {

        // Ignore.
    }

    override fun validateConfiguration(configuration: StubConfiguration) = true
}