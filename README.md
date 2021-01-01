![](Logo.png)

# Mail Server Factory

Instantiate, configure and distribute mail server configuration.

Information about Mail Server Factory: 

- [current version](./version.txt) and
- [version code](./version_code.txt).

# Compatibility

Mail Server Factory works on the following target installation operating systems:

- CentOS Server 7 and 8
- Fedora Server versions: 30 to 33
- Fedora Workstation versions: 30 to 33
- Ubuntu Desktop 20

# Specifications

Installed mail server will be delivered with the following technology stack:

- [Docker](https://www.docker.com/) running all stack services
- [PostgreSQL](https://www.postgresql.org/) for main database
- [Dovecot](https://www.dovecot.org/) and [Postfix](http://www.postfix.org/) as main mail services
- [Rspamd](https://www.rspamd.com/) for anti-spam
- [Redis](https://redis.io/) as in-memory database for [Rspamd](https://www.rspamd.com/)
- [ClamAV](https://www.clamav.net/) for anti-virus.

# Web setup

```
$ Tbd.
```

# Hot to use

Tbd.

# Running tests

```
$ ./gradlew test
```

## Requirements

Tbd.