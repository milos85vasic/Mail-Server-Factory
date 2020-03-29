package net.milosvasic.factory.mail.os

enum class OSType(val osName: String) {

    CENTOS("CentOS"),
    UBUNTU("Ubuntu"),
    FEDORA("Fedora"),
    REDHAT("RedHat"),
    UNKNOWN("Unknown"),
    GENERIC("Generic Linux distribution")
}