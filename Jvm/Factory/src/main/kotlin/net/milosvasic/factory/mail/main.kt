@file:JvmName("Launcher")

package net.milosvasic.factory.mail

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import net.milosvasic.factory.mail.configuration.Configuration
import net.milosvasic.factory.mail.error.ERROR
import net.milosvasic.logger.ConsoleLogger
import net.milosvasic.logger.FilesystemLogger
import java.io.File

fun main(args: Array<String>) {

    initLogging()
    if (args.isEmpty()) {

        fail(ERROR.EMPTY_DATA)
    } else {

        val configurationFileName = args[0]
        val configurationFile = File(configurationFileName)
        if (configurationFile.exists()) {
            log.v("Configuration file: $configurationFileName")
            val configurationJson = configurationFile.readText()
            val gson = Gson()
            try {
                val configuration = gson.fromJson(configurationJson, Configuration::class.java)

            } catch (e: JsonSyntaxException) {
                fail(e)
            }
        } else {

            fail(ERROR.FILE_DOES_NOT_EXIST, configurationFile.absolutePath)
        }
    }
}

fun initLogging() {

    val console = ConsoleLogger()
    val filesystem = FilesystemLogger(File("."))
    compositeLogger.addLoggers(console, filesystem)
}