package net.milosvasic.factory.test

import net.milosvasic.factory.compositeLogger
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