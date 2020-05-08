package net.milosvasic.factory.mail.operation

import net.milosvasic.factory.mail.EMPTY

open class OperationResult(
    val operation: Operation,
    val success: Boolean,
    val data: String = String.EMPTY,
    val exception: Exception? = null
) {

    override fun toString(): String {
        return "OperationResult(operation=$operation, success=$success, data='$data', exception=$exception)"
    }
}