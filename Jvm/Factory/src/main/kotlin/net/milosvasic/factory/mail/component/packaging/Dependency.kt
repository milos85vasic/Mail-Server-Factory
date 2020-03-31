package net.milosvasic.factory.mail.component.packaging

import net.milosvasic.factory.mail.component.packaging.item.InstallationItem

interface Dependency {

    fun getDependencies(): List<List<InstallationItem>>
}