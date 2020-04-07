package net.milosvasic.factory.mail.os

enum class Architecture(val arch: String) {

    UNKNOWN("unknown"),
    X86_64("amd64"),
    ARMHF("armhf"),
    ARM64("arm64"),
    PPC64EL("ppc64el"),
    S390X("s390x")
}