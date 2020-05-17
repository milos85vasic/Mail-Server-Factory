package net.milosvasic.factory.mail.test

import net.milosvasic.factory.mail.component.Toolkit
import net.milosvasic.factory.mail.component.installer.step.InstallationStepFactory
import net.milosvasic.factory.mail.execution.flow.callback.FlowCallback
import net.milosvasic.factory.mail.execution.flow.implementation.InstallationStepFlow
import net.milosvasic.factory.mail.log
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.operation.OperationResultListener
import net.milosvasic.factory.mail.test.implementation.StubConnection
import org.junit.jupiter.api.Test

class SkipConditionCheckStepTest : BaseTest() {

    private val iterations = 5
    private val connection = StubConnection()
    private val toolkit = Toolkit(connection)
    private val factory = InstallationStepFactory()
    private val mainFlow = InstallationStepFlow(toolkit)
    private val positiveFlow = InstallationStepFlow(toolkit)
    private val negativeFlow = InstallationStepFlow(toolkit)

    @Test
    fun testSkipConditionCheck() {
        initLogging()
        log.i("Skip condition check step test started")

        var executed = 0
        var finished = false

        val operationResultListener = object : OperationResultListener {
            override fun onOperationPerformed(result: OperationResult) {
                assert(result.success)
                executed++
            }
        }

        connection.terminal.subscribe(operationResultListener)

        val flowCallback = object : FlowCallback<String> {

            override fun onFinish(success: Boolean, message: String, data: String?) {
                if (!success) {
                    log.e(message)
                }
                assert(success)
                finished = true
            }
        }

        log.i("Skip condition check step test completed")
    }
}