package net.milosvasic.factory.configuration

import net.milosvasic.factory.EMPTY
import net.milosvasic.factory.common.busy.Busy
import net.milosvasic.factory.common.busy.BusyException
import net.milosvasic.factory.common.busy.BusyWorker
import net.milosvasic.factory.common.initialization.Initialization
import net.milosvasic.factory.log
import java.io.File

object ConfigurationManager : Initialization {

    private val busy = Busy()
    private var configurationPath = String.EMPTY
    private var configuration: Configuration? = null
    private var configurationFactory: ConfigurationFactory<*>? = null
    private val softwareConfigurations = mutableListOf<SoftwareConfiguration>()
    private val containersConfigurations = mutableListOf<SoftwareConfiguration>()

    @Throws(IllegalArgumentException::class, BusyException::class, IllegalStateException::class)
    override fun initialize() {
        checkInitialized()
        BusyWorker.busy(busy)
        val file = File(configurationPath)
        if (configurationFactory == null) {

            throw IllegalStateException("Configuration factory was not provided")
        }
        configuration = configurationFactory?.obtain(file)
        configuration?.let { config ->
            config.software?.forEach {
                val path = Configuration.getConfigurationFilePath(it)
                val softwareConfiguration = SoftwareConfiguration.obtain(path)
                val variables = softwareConfiguration.variables
                config.mergeVariables(variables)
                softwareConfigurations.add(softwareConfiguration)
            }
            config.containers?.forEach {
                val path = Configuration.getConfigurationFilePath(it)
                val containerConfiguration = SoftwareConfiguration.obtain(path)
                val variables = containerConfiguration.variables
                config.mergeVariables(variables)
                containersConfigurations.add(containerConfiguration)
            }
            printVariableNode(config.variables)
        }
        if (configuration == null) {

            throw IllegalStateException("Configuration was not initialised")
        }
        BusyWorker.free(busy)
    }

    @Throws(IllegalStateException::class)
    fun getConfiguration(): Configuration {
        checkNotInitialized()
        configuration?.let {
            return it
        }
        throw IllegalStateException("No configuration available")
    }

    fun getSoftwareConfiguration() = softwareConfigurations

    fun getContainerConfiguration() = containersConfigurations

    @Synchronized
    override fun isInitialized(): Boolean {
        return configuration != null
    }

    @Synchronized
    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    fun setConfigurationPath(path: String) {
        checkInitialized()
        val validator = ConfigurationPathValidator()
        if (validator.validate(path)) {
            configurationPath = path
        }
    }

    @Synchronized
    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    fun setConfigurationFactory(factory: ConfigurationFactory<*>) {
        checkInitialized()
        configurationFactory = factory
    }

    @Synchronized
    @Throws(IllegalStateException::class)
    override fun checkInitialized() {
        if (isInitialized()) {
            throw IllegalStateException("Configuration manager has been already initialized")
        }
    }

    @Synchronized
    @Throws(IllegalStateException::class)
    override fun checkNotInitialized() {
        if (!isInitialized()) {
            throw IllegalStateException("Configuration manager has not been initialized")
        }
    }

    private fun printVariableNode(variableNode: VariableNode?, prefix: String = String.EMPTY) {
        val prefixEnd = "-> "
        variableNode?.let { node ->
            if (node.value != String.EMPTY) {
                val printablePrefix = if (prefix != String.EMPTY) {
                    " $prefix $prefixEnd"
                } else {
                    " "
                }
                node.value.let { value ->
                    val nodeValue = Variable.parse(value.toString())
                    node.name.let { name ->
                        if (name != String.EMPTY) {
                            log.v("Configuration variable:$printablePrefix$name -> $nodeValue")
                        }
                    }
                }
            }
            node.children.forEach { child ->
                var nextPrefix = prefix
                if (nextPrefix != String.EMPTY && !nextPrefix.endsWith(prefixEnd)) {
                    nextPrefix += " $prefixEnd"
                }
                nextPrefix += node.name
                printVariableNode(child, nextPrefix)
            }
        }
    }
}