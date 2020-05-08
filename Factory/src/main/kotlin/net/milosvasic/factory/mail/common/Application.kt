package net.milosvasic.factory.mail.common

import net.milosvasic.factory.mail.common.initialization.Initialization
import net.milosvasic.factory.mail.common.initialization.Termination

interface Application : Initialization, Termination {

    fun run(args: Array<String>)

    fun onStop()
}