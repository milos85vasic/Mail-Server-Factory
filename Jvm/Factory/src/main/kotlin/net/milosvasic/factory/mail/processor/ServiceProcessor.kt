package net.milosvasic.factory.mail.processor

import net.milosvasic.factory.mail.service.Service
import net.milosvasic.factory.mail.log

class ServiceProcessor : Processor<Service> {

    override fun process(what: Service) {

        log.d("Processing ${what.getType().type.toLowerCase()} service: ${what.name}")

    }
}