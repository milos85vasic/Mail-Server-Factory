package net.milosvasic.factory.mail.common

interface SelfExecutionParametrized<T>  {

    fun execute(vararg params: T)
}