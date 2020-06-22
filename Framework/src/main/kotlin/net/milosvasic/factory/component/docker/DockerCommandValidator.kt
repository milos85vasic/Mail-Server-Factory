package net.milosvasic.factory.component.docker

import net.milosvasic.factory.common.Validation

class DockerCommandValidator : Validation<DockerCommandBuilder> {

    @Throws(IllegalArgumentException::class)
    override fun validate(vararg what: DockerCommandBuilder): Boolean {

        what.forEach {
            validate(it)
        }
        return true
    }

    @Throws(IllegalArgumentException::class)
    private fun validate(what: DockerCommandBuilder): Boolean {

        val name = what.getName()
        val image = what.getImage()
        val volume = what.getVolume()
        val command = what.getCommand()
        val run = DockerCommand.RUN.obtain()

        if (volume.isNotEmpty() && command != DockerCommand.RUN) {
            throw IllegalArgumentException("Volume can be used only with '$run' command")
        }
        if (name.isNotEmpty() && command != DockerCommand.RUN) {
            throw IllegalArgumentException("Container name can be used only with '$run' command")
        }
        if (image.isEmpty() && command == DockerCommand.RUN) {
            throw IllegalArgumentException("Cannot '$run' without image provided")
        }
        return true
    }
}