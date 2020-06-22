package net.milosvasic.factory.component.packaging

import net.milosvasic.factory.operation.Operation

data class PackageManagerOperation(val type: PackageManagerOperationType) : Operation()