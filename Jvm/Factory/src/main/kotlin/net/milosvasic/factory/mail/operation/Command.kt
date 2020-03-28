package net.milosvasic.factory.mail.operation

import net.milosvasic.factory.mail.operation.Operation

open class Command(val toExecute: String) : Operation()