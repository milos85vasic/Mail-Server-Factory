package net.milosvasic.factory.mail.operation

import net.milosvasic.factory.mail.EMPTY
import java.lang.Exception

data class OperationResult(
    val operation: Operation,
    val success: Boolean,
    val data: String = String.EMPTY,
    val exception: Exception? = null
)