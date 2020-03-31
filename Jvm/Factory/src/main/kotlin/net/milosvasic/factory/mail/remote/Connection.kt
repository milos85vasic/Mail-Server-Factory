package net.milosvasic.factory.mail.remote

import net.milosvasic.factory.mail.common.Executor
import net.milosvasic.factory.mail.os.OperatingSystem

interface Connection : Executor<String> {

    fun getRemote(): Remote

    fun getRemoteOS(): OperatingSystem
}