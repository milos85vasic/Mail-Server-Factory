package net.milosvasic.factory.mail.terminal.command

import net.milosvasic.factory.mail.EMPTY
import net.milosvasic.factory.mail.localhost
import net.milosvasic.factory.mail.remote.Remote

object Commands {

    const val rm = "rm"
    const val here = "."
    const val cp = "cp -R"
    const val find = "find "
    const val ssh = "ssh -p"
    const val scp = "scp -P"
    const val uname = "uname"
    const val mkdir = "mkdir -p"
    const val chmod = "chmod -R"
    const val chgrp = "chgrp -R"
    const val chown = "chown -R"
    const val tarCompress = "tar -cjf"
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

    fun cp(what: String, where: String): String {

        return "$cp $what $where"
    }

    fun find(what: String, where: String) = "$find$where -name \"$what\""

    fun tar(what: String, where: String): String {

        val destination = where.replace(".tar", "").replace(".gz", "")
        return "$tarCompress $destination$tarExtension -C $what ."
    }

    fun unTar(what: String, where: String) = "$tarDecompress $what -C $where"

    fun rm(what: String) = "$rm $what"

    fun chmod(where: String, mode: String) = "$chmod $mode $where"

    fun chgrp(group: String, directory: String) = "$chgrp $group $directory"

    fun chown(account: String, directory: String) = "$chown $account $directory"

    fun mkdir(path: String) = "$mkdir $path"

    fun concatenate(vararg commands: String): String {
        var result = String.EMPTY
        commands.forEach {
            if (result.isNotEmpty() && !result.isBlank()) {
                result += "; "
            }
            result += it
        }
        return result
    }

    fun test(what: String) = "test -e $what"
}