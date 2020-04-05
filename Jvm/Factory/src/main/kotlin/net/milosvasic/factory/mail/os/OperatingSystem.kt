package net.milosvasic.factory.mail.os

data class OperatingSystem(
    private var name: String = "System unknown",
    private var type: OSType = OSType.UNKNOWN,
    private var architecture: Architecture = Architecture.UNKNOWN
) {

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

                when (arch) {
                    "x8664" -> {
                        architecture = Architecture.X86_64
                    }
                    Architecture.X86_64.arch -> {
                        architecture = Architecture.X86_64
                    }
                    Architecture.ARMHF.arch -> {
                        architecture = Architecture.ARMHF
                    }
                    Architecture.ARM64.arch -> {
                        architecture = Architecture.ARM64
                    }
                    Architecture.PPC64EL.arch -> {
                        architecture = Architecture.PPC64EL
                    }
                    Architecture.S390X.arch -> {
                        architecture = Architecture.S390X
                    }
                }
            }
        }
    }

    fun getName() = name

    fun getType() = type

    fun getArchitecture() = architecture
}