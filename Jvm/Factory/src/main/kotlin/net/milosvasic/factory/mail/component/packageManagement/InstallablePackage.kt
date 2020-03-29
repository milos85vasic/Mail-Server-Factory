package net.milosvasic.factory.mail.component.packageManagement

open class InstallablePackage(
    val value: String,
    val isGroup: Boolean = false
) {

    override fun toString(): String {
        return "InstallableItem(value='$value', isGroup=$isGroup)"
    }
}