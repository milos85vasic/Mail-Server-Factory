package net.milosvasic.factory.mail.operation

interface OperationResultListener {

    fun onOperationPerformed(result: OperationResult)
}