package net.milosvasic.factory.mail.operation

data class OperationResult(
    val operation: Operation,
    val success: Boolean
)