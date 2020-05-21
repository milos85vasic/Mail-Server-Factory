package net.milosvasic.factory.mail.test.implementation

import net.milosvasic.factory.mail.component.docker.step.stack.Stack
import net.milosvasic.factory.mail.terminal.command.Commands

class StubStack(composeYmlPath: String) : Stack(composeYmlPath) {

    override fun getScriptContent(command: String): String {
        val bashHead = "#!/bin/sh"
        return Commands.echo("$bashHead\\n$command")
    }
}