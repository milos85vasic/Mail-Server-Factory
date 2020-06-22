package net.milosvasic.factory.component.docker.step

import net.milosvasic.factory.component.installer.step.RemoteOperationInstallationStep
import net.milosvasic.factory.remote.Connection

abstract class DockerInstallationStep : RemoteOperationInstallationStep<Connection>()