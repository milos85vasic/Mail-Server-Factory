package net.milosvasic.factory.mail.terminal

import net.milosvasic.factory.mail.common.Execution
import net.milosvasic.factory.mail.common.Notifying
import net.milosvasic.factory.mail.common.Subscription
import net.milosvasic.factory.mail.execution.TaskExecutor
import net.milosvasic.factory.mail.log
import net.milosvasic.factory.mail.remote.operation.OperationResult
import net.milosvasic.factory.mail.remote.operation.OperationResultListener
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStreamReader

class Terminal :
    Execution<Command>,
    Subscription<OperationResultListener>,
    Notifying<OperationResult> {

    private val runtime = Runtime.getRuntime()
    private val subscribers = mutableSetOf<OperationResultListener>()
    private val executor = TaskExecutor.instantiate(1)

    override fun execute(what: Command) {
        executor.execute {
            val commands = what.toExecute
            val process = runtime.exec(commands)
            val stdIn = BufferedReader(InputStreamReader(process.inputStream))
            val stdErr = BufferedReader(InputStreamReader(process.errorStream))
            readToLog(stdIn)
            readToLog(stdErr)
        }
    }

    override fun subscribe(what: OperationResultListener) {
        subscribers.add(what)
    }

    override fun unsubscribe(what: OperationResultListener) {
        subscribers.remove(what)
    }

    override fun notify(data: OperationResult) {
        val iterator = subscribers.iterator()
        while (iterator.hasNext()) {
            val listener = iterator.next()
            listener.onOperationPerformed(data)
        }
    }

    private fun readToLog(reader: BufferedReader) {
        var s = reader.readLine()
        while (s != null) {
            log.v(s)
            s = reader.readLine()
        }
    }
}