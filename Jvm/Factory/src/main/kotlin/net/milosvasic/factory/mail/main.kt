@file:JvmName("Launcher")

package net.milosvasic.factory.mail

import net.milosvasic.factory.mail.error.ERROR
import java.io.File

fun main(args: Array<String>) {

    if (args.isEmpty()) {

        fail(ERROR.EMPTY_DATA)
    } else {

        val configurationFileName = args[0]
        val configurationFile = File(configurationFileName)
        if (configurationFile.exists()) {


        } else {

            fail(ERROR.FILE_DOES_NOT_EXIST, configurationFile.absolutePath)
        }
    }
}