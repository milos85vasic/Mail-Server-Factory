package net.milosvasic.factory.mail.operation

open class Command(val toExecute: String) : Operation() {

    override fun toString(): String {
        return "Command(toExecute='$toExecute')"
    }
}