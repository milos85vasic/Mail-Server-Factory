package net.milosvasic.factory.mail.component.docker

enum class DockerCommand(val command: String) {

    DOCKER("docker"),
    RUN("run"),
    PS("ps"),
    BUILD("build"),
    UP("up"),
    DOWN("down"),
    COMMIT("commit"),
    COMPOSE("/usr/local/bin/docker-compose"),
    START("start"),
    STOP("stop"),
    KILL("kill"),
    VERSION("--version")
}