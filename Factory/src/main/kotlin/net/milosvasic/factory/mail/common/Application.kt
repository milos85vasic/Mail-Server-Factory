package net.milosvasic.factory.mail.common

import net.milosvasic.factory.mail.component.Initialization
import net.milosvasic.factory.mail.component.Termination

interface Application : Initialization, Termination {

    fun run(args: Array<String>)

    fun onStop()
}