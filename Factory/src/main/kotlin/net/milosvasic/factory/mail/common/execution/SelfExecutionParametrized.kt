package net.milosvasic.factory.mail.common.execution

interface SelfExecutionParametrized<T>  {

    fun execute(vararg params: T)
}