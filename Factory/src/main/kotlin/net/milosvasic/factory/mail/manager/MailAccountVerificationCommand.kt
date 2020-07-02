package net.milosvasic.factory.mail.manager

import net.milosvasic.factory.mail.account.MailAccount
import net.milosvasic.factory.terminal.TerminalCommand
import net.milosvasic.factory.terminal.command.Commands

class MailAccountVerificationCommand(account: MailAccount) : TerminalCommand(

"${Commands.echo(account.getCredentials().value)} | doveadm auth test ${account.name}"
)