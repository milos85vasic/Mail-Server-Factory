package net.milosvasic.factory.mail.component.packaging.item

data class Packages(private val packages: Envelope) : Package(packages.getValue())