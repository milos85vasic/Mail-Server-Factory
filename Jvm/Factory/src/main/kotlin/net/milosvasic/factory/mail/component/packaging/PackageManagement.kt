package net.milosvasic.factory.mail.component.packaging

import net.milosvasic.factory.mail.component.packaging.item.*

interface PackageManagement<T> {

    fun install(packages: Packages)

    fun install(packages: List<Package>)

    fun uninstall(packages: List<Package>)

    fun groupInstall(groups: List<Group>)

    fun groupUninstall(groups: List<Group>)

    fun install(vararg items: InstallationItem)

    fun notify(success: Boolean)
}