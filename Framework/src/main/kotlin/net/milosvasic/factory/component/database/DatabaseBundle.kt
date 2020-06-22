package net.milosvasic.factory.component.database

import net.milosvasic.factory.common.Bundle
import net.milosvasic.factory.remote.Connection

data class DatabaseBundle(
        val database: Database,
        val connection: Connection
) : Bundle()