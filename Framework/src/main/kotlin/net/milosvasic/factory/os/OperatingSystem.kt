package net.milosvasic.factory.os

import net.milosvasic.factory.EMPTY

data class OperatingSystem(
        private var name: String = "System unknown",
        private var type: OSType = OSType.UNKNOWN,
        private var architecture: Architecture = Architecture.UNKNOWN,
        private var hostname: String = String.EMPTY
) {

    @Throws(IllegalArgumentException::class)
    fun setHostname(data: String) {

        val validator = HostNameValidator()
        if (validator.validate(data)) {

            hostname = data
        } else {

            throw IllegalArgumentException("Invalid hostname: $data")
        }
    }

    fun parseAndSetSystemInfo(data: String) {
        val osLineString = "Operating System:"
        val archLineString = "Architecture:"
        val lines = data.split("\n")
        lines.forEach {
            if (it.contains(osLineString)) {
                name = it.replace(osLineString, "").trim()
                if (name.toLowerCase().contains(OSType.CENTOS.osName.toLowerCase())) {
                    type = OSType.CENTOS
                }
                if (name.toLowerCase().contains(OSType.FEDORA.osName.toLowerCase())) {
                    type = OSType.FEDORA
                }
                if (name.toLowerCase().contains(OSType.REDHAT.osName.toLowerCase())) {
                    type = OSType.REDHAT
                }
                if (name.toLowerCase().contains(OSType.UBUNTU.osName.toLowerCase())) {
                    type = OSType.UBUNTU
                }
                if (name.toLowerCase().contains(OSType.DEBIAN.osName.toLowerCase())) {
                    type = OSType.DEBIAN
                }
            }
            if (it.contains(archLineString)) {
                val arch = it.replace(archLineString, "")
                        .replace("-", "")
                        .replace("_", "")
                        .trim()
                        .toLowerCase()

                when {
                    arch.startsWith("x8664") -> {
                        architecture = Architecture.X86_64
                    }
                    arch.startsWith(Architecture.X86_64.arch) -> {
                        architecture = Architecture.X86_64
                    }
                    arch.startsWith(Architecture.ARMHF.arch) -> {
                        architecture = Architecture.ARMHF
                    }
                    arch.startsWith(Architecture.ARM64.arch) -> {
                        architecture = Architecture.ARM64
                    }
                    arch.startsWith(Architecture.PPC64EL.arch) -> {
                        architecture = Architecture.PPC64EL
                    }
                    arch.startsWith(Architecture.S390X.arch) -> {
                        architecture = Architecture.S390X
                    }
                }
            }
        }
    }

    fun getName() = name

    fun getType() = type

    fun getHostname() = hostname

    fun getArchitecture() = architecture
}