package net.milosvasic.factory.mail.component.database.postgres

import net.milosvasic.factory.mail.common.obtain.ObtainParametrized
import net.milosvasic.factory.mail.component.database.DatabaseBundle
import net.milosvasic.factory.mail.validation.Validator

enum class PostgresCommand : ObtainParametrized<DatabaseBundle, String> {

    PSQL {

        @Throws(IllegalArgumentException::class)
        override fun obtain(vararg param: DatabaseBundle): String {

            Validator.Arguments.validateEmpty(param)
            return "psql"
        }
    }
}