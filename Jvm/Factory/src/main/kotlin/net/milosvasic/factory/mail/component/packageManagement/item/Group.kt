package net.milosvasic.factory.mail.component.packageManagement.item

class Group(value: String) : InstallationItem(value, true) {

    override fun toString(): String {
        return "Group(value='$value', isGroup=$isGroup)"
    }
}