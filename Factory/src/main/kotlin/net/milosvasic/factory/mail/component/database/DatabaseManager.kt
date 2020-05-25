package net.milosvasic.factory.mail.component.database

import net.milosvasic.factory.mail.common.Registration
import net.milosvasic.factory.mail.common.obtain.ObtainParametrized

object DatabaseManager :
        ObtainParametrized<Type, Database>,
        Registration<Database>
{

    private val databases = mutableMapOf<Type, Database>()

    override fun register(what: Database) {
        TODO("Not yet implemented")
    }

    override fun unregister(what: Database) {
        TODO("Not yet implemented")
    }

    override fun obtain(vararg param: Type): Database {
        TODO("Not yet implemented")
    }
}