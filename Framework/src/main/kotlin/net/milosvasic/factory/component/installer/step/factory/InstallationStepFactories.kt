package net.milosvasic.factory.component.installer.step.factory

import net.milosvasic.factory.component.installer.step.InstallationStep
import net.milosvasic.factory.configuration.InstallationStepDefinition
import net.milosvasic.factory.log
import net.milosvasic.factory.validation.Validator

object InstallationStepFactories : InstallationStepFactory {

    private val mainFactory = MainInstallationStepFactory()
    private val factories = mutableListOf<InstallationStepFactory>(mainFactory)

    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    override fun obtain(vararg param: InstallationStepDefinition): InstallationStep<*> {

        Validator.Arguments.validateSingle(param)
        val definition = param[0]

        factories.forEach { factory ->
            try {
                return factory.obtain(definition)

            } catch (e: IllegalArgumentException) {

                log.e(e)
            } catch (e: IllegalStateException) {

                log.e(e)
            }
        }
        throw IllegalArgumentException("Unknown installation step type: ${definition.type}")
    }

    fun addFactory(factory: InstallationStepFactory) {

        if (!factories.contains(factory)) {
            factories.add(factory)
        }
    }

    fun removeFactory(factory: InstallationStepFactory) {

        factories.remove(factory)
    }
}