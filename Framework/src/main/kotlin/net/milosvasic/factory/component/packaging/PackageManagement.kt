package net.milosvasic.factory.component.packaging

import net.milosvasic.factory.component.packaging.item.Group
import net.milosvasic.factory.component.packaging.item.InstallationItem
import net.milosvasic.factory.component.packaging.item.Package
import net.milosvasic.factory.component.packaging.item.Packages

interface PackageManagement<T> {

    fun install(packages: Packages)

    fun install(packages: List<Package>)

    fun uninstall(packages: List<Package>)

    fun groupInstall(groups: List<Group>)

    fun groupUninstall(groups: List<Group>)

    fun install(vararg items: InstallationItem)

    fun notify(success: Boolean)
}