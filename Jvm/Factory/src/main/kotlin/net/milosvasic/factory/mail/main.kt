@file:JvmName("Launcher")

package net.milosvasic.factory.mail

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import net.milosvasic.factory.mail.configuration.Configuration
import net.milosvasic.factory.mail.error.ERROR
import net.milosvasic.factory.mail.execution.TaskExecutor
import net.milosvasic.factory.mail.processor.ServiceProcessor
import net.milosvasic.factory.mail.remote.operation.OperationResult
import net.milosvasic.factory.mail.remote.operation.OperationResultListener
import net.milosvasic.factory.mail.remote.operation.TestOperation
import net.milosvasic.factory.mail.remote.ssh.SSH
import net.milosvasic.logger.ConsoleLogger
import net.milosvasic.logger.FilesystemLogger
import java.io.File
import kotlin.system.exitProcess

fun main(args: Array<String>) {

    initLogging()
    log.i("START")
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
                log.v(configuration.name)

                val ssh = SSH(configuration.remote)
                val processor = ServiceProcessor(ssh)

                val listener = object : OperationResultListener {
                    override fun onOperationPerformed(result: OperationResult) {
                        when (result.operation) {
                            is TestOperation -> {
                                if (result.success) {

                                    log.v("Connected to: ${configuration.remote}")
                                    configuration.services.forEach {
                                        processor.process(it)
                                    }
                                } else {

                                    log.e("Could not connect to: ${configuration.remote}")
                                }
                            }
                            else -> {
                                log.e("Unexpected operation has been performed: ${result.operation}")
                            }
                        }
                        ssh.unsubscribe(this)
                        finish()
                    }
                }

                ssh.subscribe(listener)
                ssh.test()
            } catch (e: JsonSyntaxException) {
                fail(e)
            }
        } else {

            fail(ERROR.FILE_DOES_NOT_EXIST, configurationFile.absolutePath)
        }
    }
}

private fun finish() {
    log.i("FINISHED")
    exitProcess(0)
}

private fun initLogging() {
    val console = ConsoleLogger()
    val filesystem = FilesystemLogger(File("."))
    compositeLogger.addLoggers(console, filesystem)
}