package net.milosvasic.factory.component.packaging.item

open class Package(value: String) : InstallationItem(value, false) {

    override fun toString(): String {
        return "Package(value='$value', isGroup=$isGroup)"
    }
}