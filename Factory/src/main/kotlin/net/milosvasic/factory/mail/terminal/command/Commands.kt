package net.milosvasic.factory.mail.terminal.command

import net.milosvasic.factory.mail.EMPTY
import net.milosvasic.factory.mail.localhost
import net.milosvasic.factory.mail.remote.Remote
import java.io.File

object Commands {

    const val rm = "rm"
    const val here = "."
    const val cp = "cp -R"
    const val ssh = "ssh -p"
    const val scp = "scp -P"
    const val uname = "uname"
    const val hostname = "hostname"
    const val tarExtension = ".tar.gz"

    private const val find = "find "
    private const val mkdir = "mkdir -p"
    private const val chmod = "chmod -R"
    private const val chgrp = "chgrp -R"
    private const val chown = "chown -R"
    private const val openssl = "openssl"
    private const val tarCompress = "tar -cjf"
    private const val tarDecompress = "tar -xvf"

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

    fun setHostName(hostname: String) = "hostnamectl set-hostname $hostname"

    fun cat(what: String) = "cat $what"

    fun generatePrivateKey(path: String, name: String): String {

        var fullName = name
        val extension = ".key"
        if (!fullName.endsWith(extension)) {
            fullName += extension
        }
        return "$openssl genrsa -out $path${File.separator}$fullName"
    }
}