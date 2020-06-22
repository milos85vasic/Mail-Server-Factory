package net.milosvasic.factory.operation

import net.milosvasic.factory.EMPTY

open class OperationResult(
    val operation: Operation,
    val success: Boolean,
    val data: String = String.EMPTY,
    val exception: Exception? = null,
    val errorData: String = String.EMPTY
) {

    override fun toString(): String {
        return "OperationResult(operation=$operation, success=$success, data='$data', exception=$exception, errorData='$errorData')"
    }
}