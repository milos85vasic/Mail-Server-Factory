![](Logo.png)

# Mail Server Factory

Instantiate, configure and distribute mail server configuration.

Mail Server Factory version information: 

- [Current version](./version.txt)
- [Current version code](./version_code.txt)
- [Releases](https://github.com/milos85vasic/Mail-Server-Factory/releases).

# Compatibility

Mail Server Factory works on the following target installation operating systems:

- CentOS Server 7 and 8
- Fedora Server versions: 30 to 33
- Fedora Workstation versions: 30 to 33
- Ubuntu Desktop 20

# Specifications

Installed mail server will be delivered with the following technology stack:

- [Docker](https://www.docker.com/) for running all stack services containers
- [PostgreSQL](https://www.postgresql.org/) for the main database
- [Dovecot](https://www.dovecot.org/) and [Postfix](http://www.postfix.org/) as the main mail services
- [Rspamd](https://www.rspamd.com/) for the anti-spam service
- [Redis](https://redis.io/) as in-memory database for [Rspamd](https://www.rspamd.com/) service
- [ClamAV](https://www.clamav.net/) for the anti-virus service.

# Web setup

```
$ Tbd.
```

# Hot to use

Tbd.

# Cloning the project

The following command clones the project and initializes all Git submodules:

```bash
mkdir Factory && cd Factory
git clone git@github.com:milos85vasic/Mail-Server-Factory.git .
git submodule init && git submodule update
```

## Building the project

To build the project cd into the directory where you have cloned the code and execute:

```bash
gradle wrapper
./gradlew assemble
```

Note: To be able to execute [Gradle](https://gradle.org/) commands, [Gradle](https://gradle.org/) must be installed on your system.

## Running tests

To execute project tests cd into the directory where you have cloned the code and execute:

```bash
./gradlew test
```

Note: In order to be able to pass tests [Docker](https://www.docker.com/) must be installed on your system.

## Git submodules

Tbd.

## Requirements

Tbd.