package net.milosvasic.factory.mail.operation

open class Command(protected val toExecute: String, val obtainCommandOutput: Boolean = false) : Operation() {

    override fun toString(): String {
        return "${this::class.simpleName}(toExecute='$toExecute')"
    }
}