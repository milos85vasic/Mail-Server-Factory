package net.milosvasic.factory.test.implementation

import net.milosvasic.factory.EMPTY
import net.milosvasic.factory.configuration.Configuration
import net.milosvasic.factory.configuration.VariableNode
import net.milosvasic.factory.remote.Remote
import java.util.concurrent.LinkedBlockingQueue

class StubConfiguration(
        name: String = String.EMPTY,
        remote: Remote,
        includes: LinkedBlockingQueue<String>?,
        software: LinkedBlockingQueue<String>?,
        containers: LinkedBlockingQueue<String>?,
        variables: VariableNode? = null

) : Configuration(name, remote, includes, software, containers, variables)