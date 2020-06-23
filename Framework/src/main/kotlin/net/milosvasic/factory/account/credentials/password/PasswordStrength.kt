package net.milosvasic.factory.account.credentials.password

enum class PasswordStrength(val value: Int) {

    UNSAFE(0),
    WEAK(1),
    MEDIUM(2),
    STRONG(3)
}