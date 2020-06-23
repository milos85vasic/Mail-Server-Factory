package net.milosvasic.factory.account.credentials

abstract class Credentials {

    abstract fun validate(): Boolean
}