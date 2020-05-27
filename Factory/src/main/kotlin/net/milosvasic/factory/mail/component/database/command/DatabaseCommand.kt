package net.milosvasic.factory.mail.component.database.command

import net.milosvasic.factory.mail.EMPTY
import net.milosvasic.factory.mail.component.database.Database
import net.milosvasic.factory.mail.component.database.DatabaseConnection
import net.milosvasic.factory.mail.terminal.TerminalCommand
import kotlin.reflect.KProperty

abstract class DatabaseCommand(
        val database: Database,
        val connection: DatabaseConnection
) : TerminalCommand(String.EMPTY) {

    override val command: String
        get() = getDatabaseCommand()

    abstract fun getDatabaseCommand(): String
}

class Delegate() {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return "$thisRef, thank you for delegating '${property.name}' to me!"
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        println("$value has been assigned to '${property.name}' in $thisRef.")
    }
}