package net.milosvasic.factory.mail.terminal

import net.milosvasic.factory.mail.common.Execution
import net.milosvasic.factory.mail.common.Notifying
import net.milosvasic.factory.mail.common.Subscription
import net.milosvasic.factory.mail.common.busy.Busy
import net.milosvasic.factory.mail.common.busy.BusyException
import net.milosvasic.factory.mail.common.busy.BusyWorker
import net.milosvasic.factory.mail.execution.TaskExecutor
import net.milosvasic.factory.mail.log
import net.milosvasic.factory.mail.operation.Command
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.operation.OperationResultListener
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.StringBuilder
import java.util.concurrent.ConcurrentLinkedQueue

class Terminal :
    Execution<Command>,
    Subscription<OperationResultListener>,
    Notifying<OperationResult> {

    private val busy = Busy()
    private val runtime = Runtime.getRuntime()
    private val executor = TaskExecutor.instantiate(1)
    private val subscribers = ConcurrentLinkedQueue<OperationResultListener>()

    @Synchronized
    @Throws(BusyException::class)
    override fun execute(what: Command) {
        BusyWorker.busy(busy)
        val action = Runnable {
            try {
                log.d(">>> ${what.toExecute}")
                val process = runtime.exec(what.toExecute)
                val stdIn = BufferedReader(InputStreamReader(process.inputStream))
                val stdErr = BufferedReader(InputStreamReader(process.errorStream))
                val obtainCommandOutput = what.obtainCommandOutput
                val inData = readToLog(stdIn, obtainCommandOutput)
                val errData = readToLog(stdErr, obtainCommandOutput)
                val noExitValue = -1
                var exitValue = noExitValue
                while (exitValue == noExitValue) {
                    try {
                        exitValue = process.exitValue()
                    } catch (e: IllegalThreadStateException) {
                        log.w(e)
                    }
                }
                val success = exitValue == 0
                val result = OperationResult(what, success, inData + errData)
                BusyWorker.free(busy)
                notify(result)
            } catch (e: Exception) {

                log.e(e)
                BusyWorker.free(busy)
                val result = OperationResult(what, false)
                notify(result)
            }
        }
        executor.execute(action)
    }

    override fun subscribe(what: OperationResultListener) {
        subscribers.add(what)
    }

    override fun unsubscribe(what: OperationResultListener) {
        subscribers.remove(what)
    }

    @Synchronized
    override fun notify(data: OperationResult) {
        val iterator = subscribers.iterator()
        while (iterator.hasNext()) {
            val listener = iterator.next()
            listener.onOperationPerformed(data)
        }
    }

    private fun readToLog(reader: BufferedReader, obtainCommandOutput: Boolean = false): String {
        val builder = StringBuilder()
        var s = reader.readLine()
        while (s != null) {
            log.v("<<< $s")
            if (obtainCommandOutput) {
                builder.append(s).append("\n")
            }
            s = reader.readLine()
        }
        return builder.toString().trim()
    }
}