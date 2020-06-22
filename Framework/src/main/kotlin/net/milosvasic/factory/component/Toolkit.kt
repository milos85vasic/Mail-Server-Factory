package net.milosvasic.factory.component

import net.milosvasic.factory.common.Bundle
import net.milosvasic.factory.component.packaging.PackageInstaller
import net.milosvasic.factory.remote.Connection

data class Toolkit(
        val connection: Connection? = null,
        val packageInstaller: PackageInstaller? = null
) : Bundle()