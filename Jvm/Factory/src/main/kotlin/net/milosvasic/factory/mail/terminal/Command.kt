package net.milosvasic.factory.mail.terminal

import net.milosvasic.factory.mail.remote.operation.Operation

open class Command(val toExecute: String) : Operation()