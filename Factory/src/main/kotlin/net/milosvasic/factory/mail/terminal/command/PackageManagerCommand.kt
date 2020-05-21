package net.milosvasic.factory.mail.terminal.command

import net.milosvasic.factory.mail.terminal.TerminalCommand

class PackageManagerCommand(packageManagerCommand: String, operation: String) :
        TerminalCommand("$packageManagerCommand $operation")