package net.milosvasic.factory.test.implementation

import net.milosvasic.factory.remote.Remote
import net.milosvasic.factory.remote.ssh.SSHCommand
import net.milosvasic.factory.terminal.TerminalCommand

class StubSSHCommand(remote: Remote, command: TerminalCommand) :
        SSHCommand(remote, command, sshCommand = command.command)
