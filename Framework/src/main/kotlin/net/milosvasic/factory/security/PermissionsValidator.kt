package net.milosvasic.factory.security

import net.milosvasic.factory.common.Validation

class PermissionsValidator : Validation<Permission> {

    @Throws(IllegalArgumentException::class)
    override fun validate(vararg what: Permission): Boolean {

        if (what.size != 3) {
            throw IllegalArgumentException("Expected 3 parameters for permissions")
        }
        what.forEach {
            if(it.value < 0 || it.value > 7) {
                throw IllegalArgumentException("Expected value between 0 and 7")
            }
        }
        return true
    }
}