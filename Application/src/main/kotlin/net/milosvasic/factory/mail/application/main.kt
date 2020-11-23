@file:JvmName("Launcher")

package net.milosvasic.factory.mail.application

import com.apple.eawt.Application
import net.milosvasic.factory.*
import net.milosvasic.factory.application.DefaultInitializationHandler
import net.milosvasic.factory.application.server_factory.ServerFactoryBuilder
import net.milosvasic.factory.common.busy.BusyException
import net.milosvasic.factory.configuration.recipe.FileConfigurationRecipe
import net.milosvasic.factory.error.ERROR
import net.milosvasic.factory.execution.flow.callback.FlowCallback
import net.milosvasic.factory.execution.flow.implementation.initialization.InitializationFlow
import net.milosvasic.factory.mail.application.server_factory.MailServerFactory
import net.milosvasic.factory.platform.OperatingSystem
import net.milosvasic.factory.platform.Platform
import net.milosvasic.factory.validation.Validator
import net.milosvasic.factory.validation.parameters.ArgumentsExpectedException
import net.milosvasic.logger.ConsoleLogger
import net.milosvasic.logger.FilesystemLogger
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO

fun main(args: Array<String>) {

    initLogging()
    try {

        val hostOS = OperatingSystem.getHostOperatingSystem()
        val iconResourceName = "assets/Logo.png"
        val iconResource = hostOS::class.java.classLoader.getResourceAsStream(iconResourceName)
        val icon = ImageIO.read(iconResource)
        if (hostOS.getPlatform() == Platform.MAC_OS) {

            System.setProperty("apple.awt.application.name", BuildInfo.versionName)
            val app = Application.getApplication()
            app.dockIconImage = icon
        }
    } catch (e: IllegalArgumentException) {

        fail(e)
    } catch (e: NullPointerException) {

        fail(e)
    } catch (e: SecurityException) {

        fail(e)
    } catch (e: IOException) {

        fail(e)
    }
    try {

        Validator.Arguments.validateNotEmpty(args)
        val file = File(args[0])
        if (file.exists()) {

            val recipe = FileConfigurationRecipe(file)
            val builder = ServerFactoryBuilder().setRecipe(recipe)
            args.forEach { arg ->

                val argumentInstallationLocation = Argument.INSTALLATION_LOCATION.get()
                if (arg.startsWith(argumentInstallationLocation)) {

                    val installationLocation = arg.trim().replace(argumentInstallationLocation, "")
                    if (installationLocation.isNotEmpty()) {

                        builder.setInstallationLocation(installationLocation)
                    }
                    log.i("Installation location: ${builder.getInstallationLocation()}")
                }
            }
            val factory = MailServerFactory(builder)

            val callback = object : FlowCallback {
                override fun onFinish(success: Boolean) {

                    if (success) {
                        try {
                            log.i("Server factory initialized")
                            factory.run()
                        } catch (e: IllegalStateException) {
                            fail(e)
                        }
                    } else {

                        fail(ERROR.INITIALIZATION_FAILURE)
                    }
                }
            }

            val handler = DefaultInitializationHandler()
            try {
                InitializationFlow()
                        .width(factory)
                        .handler(handler)
                        .onFinish(callback)
                        .run()

            } catch (e: BusyException) {

                fail(e)
            }
        } else {

            val msg = "Configuration file does not exist: ${file.absolutePath}"
            val error = IllegalArgumentException(msg)
            fail(error)
        }
    } catch (e: ArgumentsExpectedException) {

        fail(e)
    }
}

private fun initLogging() {

    tag = BuildInfo.versionName
    val console = ConsoleLogger()
    val here = File(FILE_LOCATION_HERE)
    val filesystem = FilesystemLogger(here)
    compositeLogger.addLoggers(console, filesystem)
}