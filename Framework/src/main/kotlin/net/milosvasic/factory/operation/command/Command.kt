package net.milosvasic.factory.operation.command

import net.milosvasic.factory.operation.Operation

open class Command(private val toExecute: String) : Operation() {

    override fun toString(): String {
        return "${this::class.simpleName}(toExecute='$toExecute')"
    }
}