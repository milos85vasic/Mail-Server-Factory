package net.milosvasic.factory.mail.remote.ssh

import net.milosvasic.factory.mail.remote.Remote

class SSHRemote(
    val port: Int,
    val username: String
) : Remote() {

    override fun toString(): String {
        return "SSHRemote(port=$port, username='$username', host='$host')"
    }
}