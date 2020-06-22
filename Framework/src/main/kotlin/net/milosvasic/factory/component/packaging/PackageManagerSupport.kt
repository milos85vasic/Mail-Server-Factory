package net.milosvasic.factory.component.packaging

interface PackageManagerSupport {

    fun addSupportedPackageManager(packageManager: PackageManager)

    fun removeSupportedPackageManager(packageManager: PackageManager)
}