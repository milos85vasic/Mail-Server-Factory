package net.milosvasic.factory.mail.common

import net.milosvasic.factory.mail.common.initialization.Initializer
import net.milosvasic.factory.mail.common.initialization.Termination

interface Application : Initializer, Termination {

    fun run(args: Array<String>)

    fun onStop()
}