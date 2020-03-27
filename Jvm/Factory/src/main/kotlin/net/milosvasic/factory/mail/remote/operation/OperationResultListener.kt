package net.milosvasic.factory.mail.remote.operation

import net.milosvasic.factory.mail.remote.operation.OperationResult

interface OperationResultListener {

    fun onOperationPerformed(result: OperationResult)
}