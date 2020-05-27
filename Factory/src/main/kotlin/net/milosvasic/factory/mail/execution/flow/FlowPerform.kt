package net.milosvasic.factory.mail.execution.flow

import net.milosvasic.factory.mail.common.obtain.Obtain

interface FlowPerform<T, M, D> {

    fun perform(what: M): Flow<T, D>

    fun perform(what: Obtain<M>): Flow<T, D>
}