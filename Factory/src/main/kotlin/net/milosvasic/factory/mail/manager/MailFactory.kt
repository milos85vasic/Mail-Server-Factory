package net.milosvasic.factory.mail.manager

import net.milosvasic.factory.component.Toolkit
import net.milosvasic.factory.execution.flow.callback.DieOnFailureCallback
import net.milosvasic.factory.execution.flow.implementation.InstallationStepFlow

class MailFactory(private val toolkit: Toolkit) {

    fun getMailCreationFlow(): InstallationStepFlow {

        val dieCallback = DieOnFailureCallback()
        return InstallationStepFlow(toolkit)

                .onFinish(dieCallback)
    }
}