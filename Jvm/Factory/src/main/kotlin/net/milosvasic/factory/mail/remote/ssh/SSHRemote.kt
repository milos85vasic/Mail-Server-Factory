package net.milosvasic.factory.mail.remote.ssh

import net.milosvasic.factory.mail.remote.Remote

data class SSHRemote(
    val port: Int,
    val username: String
) : Remote()