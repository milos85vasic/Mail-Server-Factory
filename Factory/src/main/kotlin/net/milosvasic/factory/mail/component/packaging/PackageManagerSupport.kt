package net.milosvasic.factory.mail.component.packaging

interface PackageManagerSupport {

    fun addSupportedPackageManager(packageManager: PackageManager)

    fun removeSupportedPackageManager(packageManager: PackageManager)
}