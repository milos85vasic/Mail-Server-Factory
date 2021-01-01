package net.milosvasic.factory.mail.application

import net.milosvasic.factory.log

object OSInit : Runnable {

    override fun run() = log.v("Starting")
}