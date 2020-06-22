package net.milosvasic.factory.test.implementation

import net.milosvasic.factory.component.docker.step.stack.Stack
import net.milosvasic.factory.terminal.command.Commands

class StubStack(composeYmlPath: String) : Stack(composeYmlPath) {

    override fun getScriptContent(command: String): String {
        val bashHead = "#!/bin/sh"
        return Commands.echo("$bashHead\\n$command")
    }
}