package net.milosvasic.factory.mail.configuration

import net.milosvasic.factory.mail.component.docker.DockerCommand

enum class VariableContext(val context: String) {

    Database("DB"),
    Server("SERVER"),
    Certification("CERTIFICATION"),
    Docker(DockerCommand.DOCKER.obtain().toUpperCase())
}