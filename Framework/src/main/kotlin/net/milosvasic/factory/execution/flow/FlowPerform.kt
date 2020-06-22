package net.milosvasic.factory.execution.flow

import net.milosvasic.factory.common.obtain.Obtain

interface FlowPerform<T, M, D> {

    fun perform(what: M): Flow<T, D>

    fun perform(what: Obtain<M>): Flow<T, D>
}