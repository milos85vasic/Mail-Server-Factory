package net.milosvasic.factory.mail.processor

import net.milosvasic.factory.mail.service.Definition
import net.milosvasic.factory.mail.log

class ServiceProcessor : Processor<Definition> {

    override fun process(what: Definition) {

        log.d("Processing ${what.getType().type.toLowerCase()} service: ${what.name}")

    }
}