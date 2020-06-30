FROM fedora:32

RUN dnf update -y
RUN dnf install -y https://download1.rpmfusion.org/free/fedora/rpmfusion-free-release-$(rpm -E %fedora).noarch.rpm https://download1.rpmfusion.org/nonfree/fedora/rpmfusion-nonfree-release-$(rpm -E %fedora).noarch.rpm
RUN dnf install -y dovecot dovecot-pgsql dovecot-pigeonhole openssl rsyslog telnet net-tools

ADD Configuration /etc/dovecot
ADD Utils /usr/local/bin
ADD Scripts/start.sh /start.sh

RUN groupadd -g 5000 vmail && useradd -g vmail -u 5000 vmail -d /home/vmail -m
RUN chgrp vmail /etc/dovecot/dovecot.conf && chmod g+r /etc/dovecot/dovecot.conf

EXPOSE {{SERVICE.MAIL_SEND.PORTS.PORT_EXPOSED_IMAPS}}

CMD sh start.sh