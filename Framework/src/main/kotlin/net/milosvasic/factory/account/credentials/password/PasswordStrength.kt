package net.milosvasic.factory.account.credentials.password

enum class PasswordStrength(val value: Int) {

    UNKNOWN(-1),
    UNSAFE(0),
    WEAK(1),
    MEDIUM(2),
    STRONG(3);

    companion object {

        fun getByValue(strength: Int): PasswordStrength {
            values().forEach {
                if (it.value == strength) {
                    return it
                }
            }
            return UNKNOWN
        }
    }
}