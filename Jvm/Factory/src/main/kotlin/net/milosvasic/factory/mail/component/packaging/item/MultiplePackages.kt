package net.milosvasic.factory.mail.component.packaging.item

data class MultiplePackages(private val packages: PackagesWrapper) : Package(packages.getValue())