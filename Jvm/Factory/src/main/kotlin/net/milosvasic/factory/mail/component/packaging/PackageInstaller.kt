package net.milosvasic.factory.mail.component.packaging

import net.milosvasic.factory.mail.component.Initialization
import net.milosvasic.factory.mail.remote.ssh.SSH

class PackageInstaller(entryPoint: SSH) : PackageManager(entryPoint), Initialization {

    private var manager: PackageManager? = null

    override fun initialize() {

    }

    override val installCommand: String
        get() {
            manager?.let {
                return it.installCommand
            }
            return ""
        }

    override val uninstallCommand: String
        get() {
            manager?.let {
                return it.uninstallCommand
            }
            return ""
        }

    override val groupInstallCommand: String
        get() {
            manager?.let {
                return it.groupInstallCommand
            }
            return ""
        }

    override val groupUninstallCommand: String
        get() {
            manager?.let {
                return it.groupUninstallCommand
            }
            return ""
        }
}