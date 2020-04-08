package net.milosvasic.factory.mail.docker

enum class DockerCommand(val command: String) {

    RUN("run"),
    BUILD("build"),
    COMMIT("commit"),
    START("start"),
    STOP("stop"),
    KILL("kill")
}