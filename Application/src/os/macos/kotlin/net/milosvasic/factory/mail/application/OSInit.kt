package net.milosvasic.factory.mail.application

import com.apple.eawt.Application
import net.milosvasic.factory.mail.application.BuildInfo
import net.milosvasic.factory.platform.OperatingSystem
import net.milosvasic.factory.platform.Platform
import java.io.IOException
import javax.imageio.ImageIO
import kotlin.jvm.Throws

object OSInit : Runnable {

    @Throws(
            IllegalArgumentException::class,
            NullPointerException::class,
            SecurityException::class,
            IOException::class
    )
    override fun run() {

        val hostOS = OperatingSystem.getHostOperatingSystem()
        val iconResourceName = "assets/Logo.png"
        val iconResource = hostOS::class.java.classLoader.getResourceAsStream(iconResourceName)
        val icon = ImageIO.read(iconResource)
        if (hostOS.getPlatform() == Platform.MAC_OS) {

            System.setProperty("apple.awt.application.name", BuildInfo.printName())
            val app = Application.getApplication()
            app.dockIconImage = icon
        }
    }
}