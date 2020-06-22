package net.milosvasic.factory.terminal.command

import net.milosvasic.factory.terminal.TerminalCommand

class PackageManagerCommand(packageManagerCommand: String, operation: String) :
        TerminalCommand("$packageManagerCommand $operation")