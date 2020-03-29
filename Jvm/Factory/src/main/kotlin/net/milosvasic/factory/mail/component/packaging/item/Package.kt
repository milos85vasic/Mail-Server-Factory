package net.milosvasic.factory.mail.component.packaging.item

class Package(value: String) : InstallationItem(value, false) {

    override fun toString(): String {
        return "Package(value='$value', isGroup=$isGroup)"
    }
}