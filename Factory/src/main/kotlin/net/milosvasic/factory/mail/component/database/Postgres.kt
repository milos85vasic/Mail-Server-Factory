package net.milosvasic.factory.mail.component.database

import net.milosvasic.factory.mail.operation.Operation
import net.milosvasic.factory.mail.remote.Connection

class Postgres(entryPoint: Connection) : Database(entryPoint) {

    override val type: Type
        get() = Type.Postgres

    override fun initialization() {
        TODO("Not yet implemented")
    }

    override fun termination() {
        TODO("Not yet implemented")
    }

    override fun getNotifyOperation(): Operation {
        TODO("Not yet implemented")
    }
}