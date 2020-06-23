package net.milosvasic.factory.account.credentials.password

import net.milosvasic.factory.account.credentials.Credentials
import net.milosvasic.factory.log

class Password(
        var value: String,
        var strength: PasswordStrength = PasswordStrength.WEAK

) : Credentials() {

    override fun validate(): Boolean {

        val validator = PasswordValidator(strength)
        return try {
            validator.validate(this)
        } catch (e: IllegalArgumentException) {

            log.e(e)
            false
        }
    }
}