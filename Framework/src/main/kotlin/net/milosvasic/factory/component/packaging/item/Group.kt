package net.milosvasic.factory.component.packaging.item

class Group(value: String) : InstallationItem(value, true) {

    companion object {

        val DEVELOPMENT = Group("Development Tools")
    }

    override fun toString(): String {
        return "Group(value='$value', isGroup=$isGroup)"
    }
}