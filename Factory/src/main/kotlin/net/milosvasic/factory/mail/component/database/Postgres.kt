package net.milosvasic.factory.mail.component.database

import net.milosvasic.factory.mail.remote.Connection

class Postgres(name: String, entryPoint: Connection) : Database(name, entryPoint) {

    override val type: Type
        get() = Type.Postgres

    override fun initialization() {

        initialized.set(true)
        onSuccessResult()
    }

    override fun termination() = initialized.set(false)
}