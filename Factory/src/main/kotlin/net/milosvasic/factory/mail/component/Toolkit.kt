package net.milosvasic.factory.mail.component

import net.milosvasic.factory.mail.common.Bundle
import net.milosvasic.factory.mail.component.packaging.PackageInstaller
import net.milosvasic.factory.mail.remote.Connection

data class Toolkit(
        val connection: Connection? = null,
        val packageInstaller: PackageInstaller? = null
) : Bundle()