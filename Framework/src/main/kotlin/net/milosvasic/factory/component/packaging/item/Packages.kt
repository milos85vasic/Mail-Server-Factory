package net.milosvasic.factory.component.packaging.item

data class Packages(private val packages: Envelope) : Package(packages.getValue())