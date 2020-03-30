package net.milosvasic.factory.mail.component.packaging

import net.milosvasic.factory.mail.component.Initialization
import net.milosvasic.factory.mail.remote.ssh.SSH
import net.milosvasic.factory.mail.terminal.Commands

class PackageInstaller(entryPoint: SSH) : PackageManager(entryPoint), Initialization {

    private var manager: PackageManager? = null
    private var iterator: Iterator<String>? = null
    private val supportedInstallers = LinkedHashSet<String>()

    init {
        supportedInstallers.addAll(listOf("dnf", "yum", "apt-get"))
    }

    override fun initialize() {
        busy()
        iterator = supportedInstallers.iterator()
        tryNext()
    }

    @Throws(IllegalStateException::class)
    private fun tryNext() {
        if (iterator == null) {
            unBusy(false)
            return
        }
        iterator?.let {
            if (it.hasNext()) {
                val item = it.next()
                command = Commands.getApplicationInfo(item)
                entryPoint.execute(command, true)
            } else {
                unBusy(true)
            }
        }
    }

    // TODO: Override super methods and connect with manager.

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

    @Throws(IllegalStateException::class)
    fun addSupportedInstaller(installer: String) {
        checkInitialized()
        supportedInstallers.add(installer)
    }

    @Throws(IllegalStateException::class)
    private fun removeSupportedInstaller(installer: String) {
        checkInitialized()
        supportedInstallers.remove(installer)
    }

    @Throws(IllegalStateException::class)
    private fun checkInitialized() {
        manager?.let {
            throw IllegalStateException("Package installer has been already initialized.")
        }
    }
}