package net.milosvasic.factory.mail.manager

import net.milosvasic.factory.EMPTY
import net.milosvasic.factory.common.obtain.Obtain
import net.milosvasic.factory.component.database.Database
import net.milosvasic.factory.component.database.DatabaseRequest
import net.milosvasic.factory.component.database.Type
import net.milosvasic.factory.component.database.manager.DatabaseManager
import net.milosvasic.factory.component.database.postgres.Postgres
import net.milosvasic.factory.component.database.postgres.PostgresInsertCommand
import net.milosvasic.factory.component.database.postgres.PostgresSelectCommand
import net.milosvasic.factory.configuration.ConfigurationManager
import net.milosvasic.factory.configuration.variable.Context
import net.milosvasic.factory.configuration.variable.Path
import net.milosvasic.factory.configuration.variable.PathBuilder
import net.milosvasic.factory.configuration.variable.Variable
import net.milosvasic.factory.execution.flow.implementation.CommandFlow
import net.milosvasic.factory.mail.account.MailAccount
import net.milosvasic.factory.mail.configuration.MailServerConfiguration
import net.milosvasic.factory.remote.Connection
import net.milosvasic.factory.terminal.TerminalCommand
import net.milosvasic.factory.terminal.command.EchoCommand

typealias MKey = net.milosvasic.factory.mail.configuration.variable.Key

class MailFactory(private val connection: Connection) {

    @Throws(IllegalStateException::class, IllegalArgumentException::class)
    fun getMailCreationFlow(): CommandFlow {

        val flow = CommandFlow()
                .width(connection)
                .perform(EchoCommand("We are about to create email accounts"))

        val configuration = ConfigurationManager.getConfiguration()
        if (configuration is MailServerConfiguration) {

            configuration.accounts?.forEach { account ->

                flow
                        .perform(getInsertDomainCommand(account))
                        .perform(getInsertAccountCommand(account))
            }
        } else {

            throw IllegalArgumentException("Unsupported configuration type: ${configuration::class.simpleName}")
        }
        return flow
    }

    private fun getInsertAccountCommand(account: MailAccount) = object : Obtain<TerminalCommand> {

        override fun obtain(): TerminalCommand {

            val email = account.name
            val domain = email.substring(email.indexOf("@") + 1)
            val address = email.substring(0, email.indexOf("@"))

            val database = getDatabase()
            if (database is Postgres) {

                val tableDomainsPath = PathBuilder()
                        .addContext(Context.Service)
                        .addContext(Context.Database)
                        .setKey(MKey.TableDomains)
                        .build()

                val tableUsersPath = PathBuilder()
                        .addContext(Context.Service)
                        .addContext(Context.Database)
                        .setKey(MKey.TableUsers)
                        .build()

                val tableUsers = Variable.get(tableUsersPath)
                val tableDomains = Variable.get(tableDomainsPath)

                val selectDomainId = PostgresSelectCommand(
                        database,
                        tableDomains,
                        "id",
                        "name",
                        domain
                )

                val selectDomainIdCmd = selectDomainId.getSelectStatement()
                return PostgresInsertCommand(
                        database,
                        tableUsers,
                        "id, domain_id, \"user\", password",
                        "DEFAULT, ($selectDomainIdCmd), '$address', '${account.getCredentials().value}'"
                )
            } else {

                throw IllegalArgumentException("Postgres database required: $database")
            }
        }
    }

    private fun getInsertDomainCommand(account: MailAccount) = object : Obtain<TerminalCommand> {

        @Throws(IllegalStateException::class)
        override fun obtain(): TerminalCommand {

            val email = account.name
            val domain = email.substring(email.indexOf("@") + 1)

            val database = getDatabase()
            if (database is Postgres) {

                val tablePath = PathBuilder()
                        .addContext(Context.Service)
                        .addContext(Context.Database)
                        .setKey(MKey.TableDomains)
                        .build()

                val table = Variable.get(tablePath)

                return PostgresInsertCommand(
                        database,
                        table,
                        "id, name",
                        "DEFAULT, '$domain'"
                )
            } else {

                throw IllegalArgumentException("Postgres database required: $database")
            }
        }
    }

    @Throws(IllegalStateException::class)
    private fun getDatabaseName(): String {

        val path = PathBuilder()
                .addContext(Context.Service)
                .addContext(Context.Database)
                .setKey(MKey.DbDirectory)
                .build()

        val name = Variable.get(path)
        if (name == String.EMPTY) {

            throw IllegalStateException("No data available for system variable: ${path.getPath()}")
        }
        return name
    }

    @Throws(IllegalStateException::class)
    private fun getDatabaseRequest() = DatabaseRequest(Type.Postgres, getDatabaseName())

    @Throws(IllegalStateException::class, IllegalArgumentException::class)
    private fun getDatabase(): Database? {

        val request = getDatabaseRequest()
        val manager = DatabaseManager.instantiate()
        return manager?.obtain(request)
    }
}