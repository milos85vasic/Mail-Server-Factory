package net.milosvasic.factory.mail.component.database

import net.milosvasic.factory.mail.common.Bundle
import net.milosvasic.factory.mail.remote.Connection

data class DatabaseBundle(
        val database: Database,
        val connection: Connection
) : Bundle()