package net.milosvasic.factory.mail.component.database.postgres

import net.milosvasic.factory.mail.common.obtain.ObtainParametrized
import net.milosvasic.factory.mail.component.database.DatabaseBundle
import net.milosvasic.factory.mail.validation.Validator
import net.milosvasic.factory.mail.validation.parameters.NoArgumentsExpectedException

enum class PostgresCommand : ObtainParametrized<DatabaseBundle, String> {

    PSQL {

        @Throws(NoArgumentsExpectedException::class)
        override fun obtain(vararg param: DatabaseBundle): String {

            Validator.Arguments.validateEmpty(*param)
            return "psql"
        }
    }
}