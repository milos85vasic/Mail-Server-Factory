package net.milosvasic.factory.mail.common

interface Application {

    fun run(args: Array<String>)

    fun onStop()
}