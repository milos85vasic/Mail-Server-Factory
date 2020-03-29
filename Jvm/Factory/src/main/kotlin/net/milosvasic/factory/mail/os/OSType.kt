package net.milosvasic.factory.mail.os

enum class OSType(val osName: String) {

    CENTOS("CentOS"),
    UBUNTU("Ubuntu"),
    DEBIAN("Debian"),
    FEDORA("Fedora"),
    REDHAT("RedHat"),
    UNKNOWN("Unknown"),
    GENERIC("Generic Linux distribution")
}