package net.milosvasic.factory.mail.component.database

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

    override fun notify(success: Boolean) {
        TODO("Not yet implemented")
    }

    override fun onFailedResult() {
        TODO("Not yet implemented")
    }

    override fun onSuccessResult() {
        TODO("Not yet implemented")
    }

    override fun isInitialized(): Boolean {
        TODO("Not yet implemented")
    }

    override fun checkInitialized() {
        TODO("Not yet implemented")
    }

    override fun checkNotInitialized() {
        TODO("Not yet implemented")
    }

}