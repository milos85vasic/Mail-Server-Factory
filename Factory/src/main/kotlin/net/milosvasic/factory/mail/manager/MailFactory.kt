package net.milosvasic.factory.mail.manager

import net.milosvasic.factory.common.obtain.Obtain
import net.milosvasic.factory.component.database.DatabaseRequest
import net.milosvasic.factory.component.database.Type
import net.milosvasic.factory.component.database.manager.DatabaseManager
import net.milosvasic.factory.component.database.postgres.Postgres
import net.milosvasic.factory.component.database.postgres.PostgresInsertCommand
import net.milosvasic.factory.configuration.ConfigurationManager
import net.milosvasic.factory.execution.flow.implementation.CommandFlow
import net.milosvasic.factory.mail.configuration.MailServerConfiguration
import net.milosvasic.factory.remote.Connection
import net.milosvasic.factory.terminal.TerminalCommand
import net.milosvasic.factory.terminal.command.EchoCommand

class MailFactory(private val connection: Connection) {

    @Throws(IllegalStateException::class, IllegalArgumentException::class)
    fun getMailCreationFlow(): CommandFlow {


        val flow =  CommandFlow()
                .width(connection)
                .perform(EchoCommand("We are about to create email accounts"))

        val configuration = ConfigurationManager.getConfiguration()
        if (configuration is MailServerConfiguration) {

            configuration.accounts?.forEach { account ->

                val commandObtain = object : Obtain<TerminalCommand> {

                    override fun obtain(): TerminalCommand {

                        val email = account.name
                        val domain = email.substring(email.indexOf("@") + 1)
                        val manager = DatabaseManager.instantiate()
                        val dbRequest = DatabaseRequest(Type.Postgres, "postfix_service")
                        val database = manager?.obtain(dbRequest)
                        if (database is Postgres) {

                            return PostgresInsertCommand(
                                    database,
                                    "postfix_service.public.mail_virtual_domains",
                                    "id, name",
                                    "DEFAULT, '$domain'"
                            )
                        } else {

                            throw IllegalArgumentException("Postgres database required: $database")
                        }
                    }
                }

                flow.perform(commandObtain)
            }
        } else {

            throw IllegalArgumentException("Unsupported configuration type: ${configuration::class.simpleName}")
        }
        return flow
    }
}