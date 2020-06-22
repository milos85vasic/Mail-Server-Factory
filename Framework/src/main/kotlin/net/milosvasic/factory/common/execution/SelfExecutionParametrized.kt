package net.milosvasic.factory.common.execution

interface SelfExecutionParametrized<T>  {

    fun execute(vararg params: T)
}