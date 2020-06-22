package net.milosvasic.factory.test.implementation

import net.milosvasic.factory.common.Application
import net.milosvasic.factory.common.busy.Busy
import net.milosvasic.factory.common.busy.BusyDelegation
import net.milosvasic.factory.common.busy.BusyException
import net.milosvasic.factory.common.busy.BusyWorker
import net.milosvasic.factory.execution.TaskExecutor
import net.milosvasic.factory.log
import net.milosvasic.factory.operation.OperationResult
import net.milosvasic.factory.operation.OperationResultListener
import java.util.concurrent.ConcurrentLinkedQueue

class SimpleInitializer(
        private val label: String,
        private val sleepTimeInMillis: Long = 100L
) : Application, BusyDelegation {

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
            Thread.sleep(sleepTimeInMillis)
            initialized = true
            log.d("Initialized: $label")
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
            Thread.sleep(sleepTimeInMillis)
            onStop()
        }
    }

    override fun onStop() {
        log.v("Finished: $label")
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
        if (isInitialized()) {
            throw IllegalStateException("Initializer has been already initialized")
        }
    }

    @Synchronized
    @Throws(IllegalStateException::class)
    override fun checkNotInitialized() {
        if (!isInitialized()) {
            throw IllegalStateException("Initializer has not been initialized")
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