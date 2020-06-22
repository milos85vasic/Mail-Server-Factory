package net.milosvasic.factory.component.packaging

import net.milosvasic.factory.component.packaging.item.InstallationItem

interface Dependency {

    fun getDependencies(): List<List<InstallationItem>>
}