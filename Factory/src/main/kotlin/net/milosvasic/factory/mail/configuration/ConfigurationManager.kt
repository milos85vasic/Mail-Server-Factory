package net.milosvasic.factory.mail.configuration

import net.milosvasic.factory.mail.EMPTY
import net.milosvasic.factory.mail.common.busy.Busy
import net.milosvasic.factory.mail.common.busy.BusyException
import net.milosvasic.factory.mail.common.busy.BusyWorker
import net.milosvasic.factory.mail.common.initialization.Initialization
import net.milosvasic.factory.mail.log
import java.io.File

object ConfigurationManager : Initialization {

    private val busy = Busy()
    private var configurationPath = String.EMPTY
    private var configuration: Configuration? = null
    private val softwareConfigurations = mutableListOf<SoftwareConfiguration>()
    private val containersConfigurations = mutableListOf<SoftwareConfiguration>()

    @Throws(IllegalArgumentException::class, BusyException::class, IllegalStateException::class)
    override fun initialize() {
        checkInitialized()
        BusyWorker.busy(busy)
        val file = File(configurationPath)
        configuration = Configuration.obtain(file)
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