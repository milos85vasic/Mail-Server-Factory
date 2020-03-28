package net.milosvasic.factory.mail.component.packageManagement

import net.milosvasic.factory.mail.remote.ssh.SSH

open class Yum(private val entryPoint: SSH) : PackageManager(entryPoint) {

}