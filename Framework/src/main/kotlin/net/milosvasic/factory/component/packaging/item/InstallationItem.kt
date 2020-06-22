package net.milosvasic.factory.component.packaging.item

abstract class InstallationItem(
    val value: String,
    val isGroup: Boolean = false
) {

    override fun toString(): String {
        return "InstallationItem(value='$value', isGroup=$isGroup)"
    }
}