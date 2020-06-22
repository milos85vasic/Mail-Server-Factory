package net.milosvasic.factory.component.database.postgres

import net.milosvasic.factory.common.obtain.ObtainParametrized
import net.milosvasic.factory.component.database.DatabaseBundle
import net.milosvasic.factory.validation.Validator
import net.milosvasic.factory.validation.parameters.NoArgumentsExpectedException

enum class PostgresCommand : ObtainParametrized<DatabaseBundle, String> {

    PSQL {

        @Throws(NoArgumentsExpectedException::class)
        override fun obtain(vararg param: DatabaseBundle): String {

            Validator.Arguments.validateEmpty(*param)
            return "psql"
        }
    }
}