package net.milosvasic.factory.mail.component.packageManagement.item

class Package(value: String) : InstallationItem(value, false) {

    override fun toString(): String {
        return "Package(value='$value', isGroup=$isGroup)"
    }
}