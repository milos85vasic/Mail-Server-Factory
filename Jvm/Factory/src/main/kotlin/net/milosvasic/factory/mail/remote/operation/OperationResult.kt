package net.milosvasic.factory.mail.remote.operation

import net.milosvasic.factory.mail.remote.operation.Operation

data class OperationResult(
    var operation: Operation,
    val success: Boolean
)