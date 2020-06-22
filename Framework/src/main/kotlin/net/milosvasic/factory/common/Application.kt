package net.milosvasic.factory.common

import net.milosvasic.factory.common.initialization.Initializer
import net.milosvasic.factory.common.initialization.Termination

interface Application : Initializer, Termination {

    fun run()

    fun onStop()
}