package net.milosvasic.factory.mail.component

abstract class Component {

    val componentId = ComponentManager.subscribe(this::class)
}