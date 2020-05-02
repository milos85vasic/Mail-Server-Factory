package net.milosvasic.factory.mail.component.docker

import net.milosvasic.factory.mail.common.Obtain
import net.milosvasic.factory.mail.configuration.*
import java.io.File

enum class DockerCommand : Obtain<String> {

    DOCKER {
        override fun obtain() = "docker"
    },
    RUN {
        override fun obtain() = "run"
    },
    PS {
        override fun obtain() = "ps"
    },
    BUILD {
        override fun obtain() = "build"
    },
    UP {
        override fun obtain() = "up"
    },
    DOWN {
        override fun obtain() = "down"
    },
    COMMIT {
        override fun obtain() = "commit"
    },
    COMPOSE {

        @Throws(IllegalStateException::class)
        override fun obtain(): String {

            val context = VariableContext.Docker.context
            val dockerComposePath = VariableKey.DOCKER_COMPOSE_PATH.key
            val configuration = ConfigurationManager.getConfiguration()
            val key = "$context${VariableNode.contextSeparator}$dockerComposePath"
            return "${configuration.getVariableParsed(key)}${File.separator}docker-compose"
        }
    },
    START {
        override fun obtain() = "start"
    },
    STOP {
        override fun obtain() = "stop"
    },
    KILL {
        override fun obtain() = "kill"
    },
    VERSION {
        override fun obtain() = "--version"
    }
}