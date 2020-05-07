package net.milosvasic.factory.mail.execution.flow

import net.milosvasic.factory.mail.common.busy.BusyException

abstract class FlowPerformBuilder<T, M, D> : FlowBuilder<T, M, D>(), FlowPerform<T, M, D> {

    @Throws(BusyException::class)
    override fun perform(what: M): Flow<T, M, D> {
        if (busy.isBusy()) {
            throw BusyException()
        }
        currentOperations.add(what)
        return this
    }

    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    override fun tryNext() {
        if (subjects.isEmpty()) {
            throw IllegalArgumentException("No subjects provided")
        }
        subjects.keys.forEach {
            if (subjects[it] == null) {
                throw IllegalArgumentException("Null operations provided for subject: $it")
            }
            subjects[it]?.let { children ->
                if (children.isEmpty()) {
                    throw IllegalArgumentException("No operations provided for subject: $it")
                }
            }
        }
        if (subjectsIterator == null) {
            subjectsIterator = subjects.keys.iterator()
        }
        if (currentSubject == null) {
            subjectsIterator?.let { sIterator ->
                if (sIterator.hasNext()) {
                    currentSubject = sIterator.next()
                } else {
                    finish(true)
                }
            }
        } else {
            if (operationsIterator == null) {
                currentSubject?.let {
                    subjects[it]?.let { operations ->
                        operationsIterator = operations.iterator()
                    }
                }
            }
        }
        if (operationsIterator == null) {
            subjects[currentSubject]?.let {
                operationsIterator = it.iterator()
            }
        }
        if (currentOperation == null) {
            operationsIterator?.let { oIterator ->
                if (oIterator.hasNext()) {
                    currentOperation = oIterator.next()
                } else {
                    currentSubject = null
                    operationsIterator = null
                    tryNext()
                    return
                }
            }
        }
        process()
    }
}