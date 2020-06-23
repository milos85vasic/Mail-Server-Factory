package net.milosvasic.factory.account

import net.milosvasic.factory.account.credentials.Credentials

abstract class Account(

        val name: String,
        protected val type: String,
        protected val credentials: String
) {

    fun getAccountType() = AccountType.getByValue(type)

    abstract fun getCredentials(): Credentials

    override fun toString(): String {
        return "Account(${printAccount()})"
    }

    protected fun printAccount() = "name='$name', credentials='$credentials', type='$type'"
}