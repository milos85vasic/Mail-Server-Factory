FROM fedora:32

RUN dnf update -y
RUN dnf install -y https://download1.rpmfusion.org/free/fedora/rpmfusion-free-release-$(rpm -E %fedora).noarch.rpm https://download1.rpmfusion.org/nonfree/fedora/rpmfusion-nonfree-release-$(rpm -E %fedora).noarch.rpm
RUN dnf install -y dovecot dovecot-pgsql dovecot-pigeonhole openssl rsyslog telnet net-tools

ADD Configuration /etc/dovecot
ADD Utils /usr/local/bin
ADD Scripts/start.sh /start.sh

ADD Logrotate/dovecot /etc/logrotate.d/dovecot
ADD Logrotate/dovecot_debug /etc/logrotate.d/dovecot_debug
ADD Logrotate/dovecot_info /etc/logrotate.d/dovecot_info

ADD Sieve/.dovecot.sieve /etc/dovecot/.dovecot.sieve

RUN groupadd -g 5000 vmail && useradd -g vmail -u 5000 vmail -d /home/vmail -m
RUN chgrp vmail /etc/dovecot/dovecot.conf && chmod g+r /etc/dovecot/dovecot.conf
RUN chgrp vmail /etc/dovecot/.dovecot.sieve && chmod g+r /etc/dovecot/.dovecot.sieve

EXPOSE {{SERVICE.MAIL_SEND.PORTS.PORT_EXPOSED_IMAPS}}

CMD sh start.sh