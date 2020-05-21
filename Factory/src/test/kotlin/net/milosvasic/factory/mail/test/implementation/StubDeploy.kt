package net.milosvasic.factory.mail.test.implementation

import net.milosvasic.factory.mail.component.installer.step.deploy.Deploy
import net.milosvasic.factory.mail.remote.Remote
import net.milosvasic.factory.mail.security.Permission
import net.milosvasic.factory.mail.security.Permissions
import net.milosvasic.factory.mail.terminal.TerminalCommand
import net.milosvasic.factory.mail.terminal.command.*

class StubDeploy(
        what: String,
        private val where: String,
        private val protoStubs: List<String>
) : Deploy(what, where) {

    override fun getScp(remote: Remote) = CpCommand(localTar, where)

    override fun getScpCommand() = Commands.cp

    override fun isRemote(operation: TerminalCommand) =
            operation.command.contains(StubSSH.stubCommandMarker)

    @Throws(IllegalArgumentException::class)
    override fun getProtoCleanup(): TerminalCommand {
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