package net.milosvasic.factory.mail

import kotlin.system.exitProcess

fun fail(error: ERROR) {

    System.err.println(error.message)
    exitProcess(error.code)
}