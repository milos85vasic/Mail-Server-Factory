package net.milosvasic.factory.mail.test.implementation

import net.milosvasic.factory.mail.common.Application
import net.milosvasic.factory.mail.common.busy.Busy
import net.milosvasic.factory.mail.common.busy.BusyDelegation
import net.milosvasic.factory.mail.common.busy.BusyException
import net.milosvasic.factory.mail.common.busy.BusyWorker
import net.milosvasic.factory.mail.configuration.ConfigurationManager
import net.milosvasic.factory.mail.execution.TaskExecutor
import net.milosvasic.factory.mail.log
import net.milosvasic.factory.mail.operation.OperationResult
import net.milosvasic.factory.mail.operation.OperationResultListener
import java.util.concurrent.ConcurrentLinkedQueue

class SimpleInitializer(val label: String) : Application, BusyDelegation {

    private val busy = Busy()
    private var initialized = false
    private val executor = TaskExecutor.instantiate(1)
    private val terminationOperation = SimpleTerminationOperation()
    private val initializationOperation = SimpleInitializationOperation()
    private val subscribers = ConcurrentLinkedQueue<OperationResultListener>()

    @Throws(IllegalStateException::class)
    override fun initialize() {
        checkInitialized()
        busy()
        executor.execute {
            log.v("Initializing: $label")
            Thread.sleep(1000)
            initialized = true
            log.v("Initialized: $label")
            notifyInit()
        }
    }

    @Throws(IllegalStateException::class)
    override fun terminate() {
        checkNotInitialized()
        if (!busy.isBusy()) {
            throw IllegalStateException("Not running")
        }
        notifyTerm()
    }

    @Synchronized
    override fun isInitialized() = initialized

    @Throws(IllegalStateException::class)
    override fun run() {
        checkNotInitialized()
        busy()
        executor.execute {
            Thread.sleep(1000)
            onStop()
        }
    }

    override fun onStop() {
        log.i("Finished")
        try {
            terminate()
        } catch (e: IllegalStateException) {

            log.e(e)
        }
    }

    @Synchronized
    @Throws(BusyException::class)
    override fun busy() {
        BusyWorker.busy(busy)
    }

    @Synchronized
    override fun free() {
        BusyWorker.free(busy)
    }

    @Synchronized
    @Throws(IllegalStateException::class)
    override fun checkInitialized() {
        if (ConfigurationManager.isInitialized()) {
            throw IllegalStateException("Server factory has been already initialized")
        }
    }

    @Synchronized
    @Throws(IllegalStateException::class)
    override fun checkNotInitialized() {
        if (!ConfigurationManager.isInitialized()) {
            throw IllegalStateException("Server factory has not been initialized")
        }
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


    private fun notifyInit() {
        free()
        val result = OperationResult(initializationOperation, true)
        notify(result)
    }

    private fun notifyTerm() {
        free()
        val result = OperationResult(terminationOperation, true)
        notify(result)
    }
}