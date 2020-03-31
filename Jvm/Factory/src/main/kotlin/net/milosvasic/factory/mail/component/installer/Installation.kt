package net.milosvasic.factory.mail.component.installer

interface Installation {

    val steps: List<InstallationStep>

    fun install()

    fun uninstall()
}