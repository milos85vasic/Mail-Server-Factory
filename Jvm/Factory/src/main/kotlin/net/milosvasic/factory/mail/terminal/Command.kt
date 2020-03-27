package net.milosvasic.factory.mail.terminal

import net.milosvasic.factory.mail.remote.operation.Operation

class Command(val toExecute: Array<String>) : Operation()