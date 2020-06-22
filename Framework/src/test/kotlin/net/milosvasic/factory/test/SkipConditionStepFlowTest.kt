package net.milosvasic.factory.test

import net.milosvasic.factory.component.Toolkit
import net.milosvasic.factory.component.installer.recipe.CommandInstallationStepRecipe
import net.milosvasic.factory.component.installer.recipe.ConditionRecipe
import net.milosvasic.factory.component.installer.step.CommandInstallationStep
import net.milosvasic.factory.component.installer.step.InstallationStepType
import net.milosvasic.factory.component.installer.step.condition.Condition
import net.milosvasic.factory.component.installer.step.condition.SkipCondition
import net.milosvasic.factory.component.installer.step.factory.InstallationStepFactories
import net.milosvasic.factory.configuration.InstallationStepDefinition
import net.milosvasic.factory.execution.flow.callback.FlowCallback
import net.milosvasic.factory.execution.flow.implementation.InstallationStepFlow
import net.milosvasic.factory.log
import net.milosvasic.factory.operation.OperationResult
import net.milosvasic.factory.operation.OperationResultListener
import net.milosvasic.factory.terminal.TerminalCommand
import net.milosvasic.factory.test.implementation.StubConnection
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

open class SkipConditionStepFlowTest : BaseTest() {

    @Test
    fun testConditionStepFlow() {
        initLogging()
        log.i("${name()} step flow test started")

        var failed = 0
        var finished = 0
        var failedTerminalCommands = 0
        var finishedTerminalCommands = 0
        val connection = StubConnection()
        val toolkit = Toolkit(connection)
        val factory = InstallationStepFactories

        val operationNResultListener = object : OperationResultListener {
            override fun onOperationPerformed(result: OperationResult) {

                when (result.operation) {
                    is TerminalCommand -> {
                        if (result.success) {
                            finishedTerminalCommands++
                        } else {
                            failedTerminalCommands++
                        }
                    }
                }
            }
        }

        val flowCallback = object : FlowCallback {

            override fun onFinish(success: Boolean) {
                if (success) {
                    finished++
                } else {
                    failed++
                }
            }
        }

        connection.getTerminal().subscribe(operationNResultListener)

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
                    .registerRecipe(
                            Condition::class,
                            ConditionRecipe::class
                    )
                    .onFinish(flowCallback)
        }

        positiveFlow.run()
        while (positiveFlow.isBusy()) {
            Thread.yield()
        }

        positiveNegativeFlow.run()
        while (positiveNegativeFlow.isBusy()) {
            Thread.yield()
        }

        negativePositiveFlow.run()
        while (negativePositiveFlow.isBusy()) {
            Thread.yield()
        }

        negativeFlow.run()
        while (negativeFlow.isBusy()) {
            Thread.yield()
        }

        while (positiveFlow.isBusy()) {
            Thread.yield()
        }

        Assertions.assertEquals(expectedPositives(), finished)
        Assertions.assertEquals(expectedNegatives(), failed)
        Assertions.assertEquals(expectedTerminalCommandPositives(), failedTerminalCommands)
        Assertions.assertEquals(expectedTerminalCommandNegatives(), finishedTerminalCommands)
        log.i("${name()} step flow test completed")
    }

    protected open fun expectedPositives() = 3

    protected open fun expectedNegatives() = 1

    protected open fun expectedTerminalCommandPositives() = expectedPositives()

    protected open fun expectedTerminalCommandNegatives() = expectedPositives()

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