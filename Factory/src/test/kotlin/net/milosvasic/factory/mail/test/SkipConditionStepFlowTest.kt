package net.milosvasic.factory.mail.test

import net.milosvasic.factory.mail.component.Toolkit
import net.milosvasic.factory.mail.component.installer.recipe.CommandInstallationStepRecipe
import net.milosvasic.factory.mail.component.installer.recipe.ConditionRecipe
import net.milosvasic.factory.mail.component.installer.step.CommandInstallationStep
import net.milosvasic.factory.mail.component.installer.step.InstallationStepFactory
import net.milosvasic.factory.mail.component.installer.step.InstallationStepType
import net.milosvasic.factory.mail.component.installer.step.condition.SkipCondition
import net.milosvasic.factory.mail.configuration.InstallationStepDefinition
import net.milosvasic.factory.mail.execution.flow.callback.FlowCallback
import net.milosvasic.factory.mail.execution.flow.implementation.InstallationStepFlow
import net.milosvasic.factory.mail.log
import net.milosvasic.factory.mail.test.implementation.StubConnection
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

open class SkipConditionStepFlowTest : BaseTest() {

    @Test
    fun testConditionStepFlow() {
        initLogging()
        log.i("${name()} step flow test started")

        var failed = 0
        var finished = 0
        val connection = StubConnection()
        val toolkit = Toolkit(connection)
        val factory = InstallationStepFactory()

        val flowCallback = object : FlowCallback<String> {

            override fun onFinish(success: Boolean, message: String, data: String?) {
                if (success) {
                    finished++
                } else {
                    failed++
                }
            }
        }

        var positiveFlow = InstallationStepFlow(toolkit)
        var definitions = getDefinitions(fails = false, alreadyInstalled = true)
        definitions.forEach { definition ->
            val installationStep = factory.obtain(definition)
            positiveFlow = positiveFlow.width(installationStep)
        }

        var positiveNegativeFlow = InstallationStepFlow(toolkit)
        definitions = getDefinitions(fails = false, alreadyInstalled = false)
        definitions.forEach { definition ->
            val installationStep = factory.obtain(definition)
            positiveNegativeFlow = positiveNegativeFlow.width(installationStep)
        }

        var negativePositiveFlow = InstallationStepFlow(toolkit)
        definitions = getDefinitions(fails = true, alreadyInstalled = true)
        definitions.forEach { definition ->
            val installationStep = factory.obtain(definition)
            negativePositiveFlow = negativePositiveFlow.width(installationStep)
        }

        var negativeFlow = InstallationStepFlow(toolkit)
        definitions = getDefinitions(fails = true, alreadyInstalled = false)
        definitions.forEach { definition ->
            val installationStep = factory.obtain(definition)
            negativeFlow = negativeFlow.width(installationStep)
        }

        listOf(
                positiveFlow,
                positiveNegativeFlow,
                negativePositiveFlow,
                negativeFlow
        ).forEach { flow ->
            flow
                    .registerRecipe(
                            CommandInstallationStep::class,
                            CommandInstallationStepRecipe::class
                    )
                    .registerRecipe(
                            SkipCondition::class,
                            ConditionRecipe::class
                    )
                    .onFinish(flowCallback)
        }

        positiveFlow
                .connect(positiveNegativeFlow)
                .connect(negativePositiveFlow)
                .connect(negativeFlow)
                .run()

        while (finished < 3 || failed < 1) {
            Thread.yield()
        }

        Assertions.assertEquals(3, finished)
        Assertions.assertEquals(1, failed)
        log.i("${name()} step flow test completed")
    }

    protected open fun name() = "Skip condition"

    protected open fun type() = InstallationStepType.SKIP_CONDITION.type

    private fun getDefinitions(fails: Boolean, alreadyInstalled: Boolean): List<InstallationStepDefinition> {
        return if (fails) {
            if (alreadyInstalled) {
                listOf(
                        InstallationStepDefinition(
                                type = type(),
                                value = "test -e ./not_existing_one"
                        ),
                        InstallationStepDefinition(
                                type = InstallationStepType.COMMAND.type,
                                value = "echo 'This one will be executed'"
                        )
                )
            } else {
                listOf(
                        InstallationStepDefinition(
                                type = type(),
                                value = "This one will fail"
                        ),
                        InstallationStepDefinition(
                                type = InstallationStepType.COMMAND.type,
                                value = "echo 'This one will be executed'"
                        )
                )
            }
        } else {
            if (alreadyInstalled) {
                listOf(
                        InstallationStepDefinition(
                                type = type(),
                                value = "echo 'Condition'"
                        ),
                        InstallationStepDefinition(
                                type = InstallationStepType.COMMAND.type,
                                value = "echo 'This one will not be executed'"
                        )
                )
            } else {
                listOf(
                        InstallationStepDefinition(
                                type = type(),
                                value = "which does_not_exist"
                        ),
                        InstallationStepDefinition(
                                type = InstallationStepType.COMMAND.type,
                                value = "echo 'This one will be executed'"
                        )
                )
            }
        }
    }
}