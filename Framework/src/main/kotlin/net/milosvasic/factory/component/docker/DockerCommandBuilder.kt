package net.milosvasic.factory.component.docker

import net.milosvasic.factory.EMPTY
import net.milosvasic.factory.common.Build

class DockerCommandBuilder : Build<String> {

    private var name = String.EMPTY
    private var image = String.EMPTY
    private var volume = String.EMPTY
    private var hostVolume = String.EMPTY
    private var guestVolume = String.EMPTY
    private var command = DockerCommand.RUN
    private var containerName = String.EMPTY

    fun getName() = name

    fun getVolume() = volume

    fun getCommand() = command

    fun getImage() = image

    fun command(command: DockerCommand): DockerCommandBuilder {
        this.command = command
        return this
    }

    fun volume(guest: String, host: String = String.EMPTY): DockerCommandBuilder {
        hostVolume = host
        guestVolume = guest
        setVolume()
        return this
    }

    fun image(image: String): DockerCommandBuilder {
        this.image = image
        return this
    }

    fun containerName(containerName: String): DockerCommandBuilder {
        this.containerName = containerName
        setName()
        return this
    }

    @Throws(IllegalArgumentException::class)
    override fun build(): String {

        val validator = DockerCommandValidator()
        if (validator.validate(this)) {
            val dockerCommand = DockerCommand.DOCKER.obtain()
            return "$dockerCommand ${command.obtain()} $volume $name $image"
        }
        return String.EMPTY
    }

    private fun setName(): String {
        name = ""
        if (containerName.isNotEmpty()) {
            name = "--name $containerName"
        }
        return name
    }

    private fun setVolume(): String {
        volume = "-v "
        if (hostVolume.isNotEmpty()) {
            volume += "$hostVolume:"
        }
        if (guestVolume.isEmpty()) {
            volume = String.EMPTY
        } else {
            volume += guestVolume
        }
        return volume
    }
}