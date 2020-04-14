package net.milosvasic.factory.mail.component.docker.step

import net.milosvasic.factory.mail.component.installer.step.RemoteOperationInstallationStep
import net.milosvasic.factory.mail.remote.Connection

abstract class DockerInstallationStep : RemoteOperationInstallationStep<Connection>()