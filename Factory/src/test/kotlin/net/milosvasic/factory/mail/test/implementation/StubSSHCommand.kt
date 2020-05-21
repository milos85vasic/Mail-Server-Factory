package net.milosvasic.factory.mail.test.implementation

import net.milosvasic.factory.mail.remote.Remote
import net.milosvasic.factory.mail.remote.ssh.SSHCommand
import net.milosvasic.factory.mail.terminal.TerminalCommand

class StubSSHCommand(remote: Remote, command: TerminalCommand) :
        SSHCommand(remote, command, sshCommand = command.command)
