package net.milosvasic.factory.account

abstract class Account(

        val name: String,
        val credentials: String,
        private val type: String
) {

    fun getAccountType() = AccountType.getByValue(type)

    override fun toString(): String {
        return "Account(${printAccount()})"
    }

    protected fun printAccount() = "name='$name', credentials='$credentials', type='$type'"
}