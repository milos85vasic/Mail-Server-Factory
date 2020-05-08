package net.milosvasic.factory.mail.test

import net.milosvasic.factory.mail.compositeLogger
import net.milosvasic.logger.ConsoleLogger
import net.milosvasic.logger.FilesystemLogger
import java.io.File

abstract class BaseTest {

    protected fun initLogging() {
        val console = ConsoleLogger()
        val filesystem = FilesystemLogger(File("."))
        compositeLogger.addLoggers(console, filesystem)
    }
}