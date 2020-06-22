package net.milosvasic.factory.test.implementation

import net.milosvasic.factory.component.installer.step.deploy.Deploy
import net.milosvasic.factory.remote.Remote
import net.milosvasic.factory.security.Permission
import net.milosvasic.factory.security.Permissions
import net.milosvasic.factory.terminal.TerminalCommand
import net.milosvasic.factory.terminal.command.ChmodCommand
import net.milosvasic.factory.terminal.command.Commands
import net.milosvasic.factory.terminal.command.CpCommand
import net.milosvasic.factory.terminal.command.RawTerminalCommand

class StubDeploy(
        what: String,
        private val where: String,
        private val protoStubs: List<String>
) : Deploy(what, where) {

    override fun getScp(remote: Remote) = CpCommand(localTar, where)

    override fun getScpCommand() = Commands.cp

    @Throws(IllegalArgumentException::class)
    override fun getProtoCleanup(): TerminalCommand {

        if (protoStubs.isEmpty()) {
            throw IllegalArgumentException("No proto stubs available")
        }
        var command = ""
        protoStubs.forEachIndexed { index, it ->
            if (index > 0) {
                command += " && "
            }
            command += Commands.rm("$where/$it")
        }
        return RawTerminalCommand(command)
    }

    override fun getSecurityChanges(remote: Remote): TerminalCommand {

        val permissions = Permissions(Permission.ALL, Permission.NONE, Permission.NONE)
        return ChmodCommand(where, permissions.obtain())
    }
}