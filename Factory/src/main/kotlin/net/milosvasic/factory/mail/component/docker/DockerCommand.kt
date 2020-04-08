package net.milosvasic.factory.mail.component.docker

enum class DockerCommand(val command: String) {

    DOCKER("docker"),
    RUN("run"),
    BUILD("build"),
    COMMIT("commit"),
    START("start"),
    STOP("stop"),
    KILL("kill"),
    VERSION("--version")
}