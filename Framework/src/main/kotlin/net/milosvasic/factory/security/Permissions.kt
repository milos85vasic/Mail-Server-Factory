package net.milosvasic.factory.security

import net.milosvasic.factory.common.obtain.Obtain

data class Permissions(
        var user: Permission,
        var group: Permission,
        var others: Permission
) : Obtain<String> {

    @Throws(IllegalArgumentException::class)
    override fun obtain(): String {

        val validator = PermissionsValidator()
        if (validator.validate(user, group, others)) {
            return "${user.value}${group.value}${others.value}"
        }
        return "000"
    }
}