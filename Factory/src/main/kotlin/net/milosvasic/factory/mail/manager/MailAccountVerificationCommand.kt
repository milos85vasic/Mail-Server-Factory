package net.milosvasic.factory.mail.manager

import net.milosvasic.factory.mail.account.MailAccount
import net.milosvasic.factory.terminal.TerminalCommand

class MailAccountVerificationCommand(account: MailAccount) : TerminalCommand(

        "doveadm auth lookup ${account.name} | grep \"${account.name}\""
)