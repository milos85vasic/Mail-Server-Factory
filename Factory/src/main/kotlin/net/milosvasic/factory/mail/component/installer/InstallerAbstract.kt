package net.milosvasic.factory.mail.component.installer

import net.milosvasic.factory.mail.common.busy.BusyWorker
import net.milosvasic.factory.mail.component.installer.step.InstallationStep
import net.milosvasic.factory.mail.configuration.ConfigurableSoftware
import net.milosvasic.factory.mail.remote.Connection

abstract class InstallerAbstract(entryPoint: Connection) :
        BusyWorker<InstallationStep<*>>(entryPoint),
        ConfigurableSoftware,
        Installation