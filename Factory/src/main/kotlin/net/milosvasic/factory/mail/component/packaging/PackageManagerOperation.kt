package net.milosvasic.factory.mail.component.packaging

import net.milosvasic.factory.mail.operation.Operation

data class PackageManagerOperation(val type: PackageManagerOperationType) : Operation()