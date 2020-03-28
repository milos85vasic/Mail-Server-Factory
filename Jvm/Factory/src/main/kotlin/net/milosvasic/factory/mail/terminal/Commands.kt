package net.milosvasic.factory.mail.terminal

import net.milosvasic.factory.mail.localhost

object Commands {

    fun echo(what: String) = "echo '$what'"

    fun ssh(user: String = "root", command: String, port: Int = 22, host: String = localhost): String {
        return "ssh -p $port $user@$host $command"
    }
}