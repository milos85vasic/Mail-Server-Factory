package net.milosvasic.factory.mail.component.packageManagement

import net.milosvasic.factory.mail.remote.ssh.SSH

open class Yum(entryPoint: SSH) : PackageManager(entryPoint) {

    override val installCommand: String
        get() = "yum install -y"

    override val uninstallCommand: String
        get() = "yum remove -y"

    override val groupInstallCommand: String
        get() = "yum groupinstall -y"

    override val groupUninstallCommand: String
        get() = "yum groupremove -y"
}