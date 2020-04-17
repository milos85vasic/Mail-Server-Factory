package net.milosvasic.factory.mail.terminal

import net.milosvasic.factory.mail.localhost
import net.milosvasic.factory.mail.remote.Remote

object Commands {

    const val rm = "rm"
    const val ssh = "ssh -p"
    const val scp = "scp -P"
    const val tar = "tar "
    const val tarDecompress = "tar -xvf"
    const val tarExtension = ".tar.gz"

    fun echo(what: String) = "echo '$what'"

    fun printf(what: String) = "printf '$what'"

    fun ssh(user: String = "root", command: String, port: Int = 22, host: String = localhost): String {
        return "$ssh $port $user@$host $command"
    }

    fun ping(host: String, timeoutInSeconds: Int = 3): String {
        return "ping $host -t$timeoutInSeconds"
    }

    fun getHostInfo(): String = "hostnamectl"

    fun getApplicationInfo(application: String): String = "which $application"

    fun reboot(rebootIn: Int = 2) = "( sleep $rebootIn ; reboot ) & "

    fun grep(what: String) = "grep \"$what\""

    fun scp(what: String, where: String, remote: Remote): String {

        return "$scp ${remote.port} $what ${remote.account}@${remote.host}:$where"
    }

    fun tar(what: String, where: String, excludes: List<String> = listOf()): String {

        var exclude = ""
        excludes.forEach {
            exclude += "--exclude='$it' "
        }
        val destination = where.replace(".tar", "").replace(".gz", "")
        return "$tar $exclude -cjf $destination$tarExtension -C $what ."
    }

    fun unTar(what: String, where: String) = "$tarDecompress $what -C $where"

    fun rm(what: String) = "$rm $what"
}