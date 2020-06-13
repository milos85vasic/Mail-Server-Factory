FROM centos:centos7.7.1908

RUN yum update -y
RUN yum --enablerepo=centosplus install -y dovecot dovecot-pgsql dovecot-pigeonhole
RUN yum install -y openssl rsyslog telnet net-tools

ADD Configuration /etc/dovecot
ADD Utils /usr/local/bin
ADD Scripts/start.sh /start.sh

RUN groupadd -g 5000 vmail && useradd -g vmail -u 5000 vmail -d /home/vmail -m
RUN chgrp vmail /etc/dovecot/dovecot.conf && chmod g+r /etc/dovecot/dovecot.conf

EXPOSE {{SERVER.DOVECOT.PORTS.POP}}
EXPOSE {{SERVER.DOVECOT.PORTS.IMAP}}
EXPOSE {{SERVER.DOVECOT.PORTS.IMAPS}}
EXPOSE {{SERVER.DOVECOT.PORTS.POP3S}}

CMD sh start.sh