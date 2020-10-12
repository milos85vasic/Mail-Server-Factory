FROM fedora:32

ARG DB
ARG DB_PORT
ARG PORT_EXPOSED_IMAPS

ENV DB "$DB"
ENV DB_PORT "$DB_PORT"
ENV PORT_EXPOSED_IMAPS "$PORT_EXPOSED_IMAPS"

RUN dnf update -y && \
    dnf install findutils -y && \
    dnf clean all && \
    dnf install -y https://download1.rpmfusion.org/free/fedora/rpmfusion-free-release-$(rpm -E %fedora).noarch.rpm https://download1.rpmfusion.org/nonfree/fedora/rpmfusion-nonfree-release-$(rpm -E %fedora).noarch.rpm && \
    dnf install -y dovecot dovecot-pgsql dovecot-pigeonhole openssl telnet net-tools iputils && \
    mkdir /etc/dovecot/sieve

ADD Configuration /etc/dovecot
ADD Utils /usr/local/bin
ADD Scripts/start.sh /start.sh
ADD Scripts/logrotate.sh /logrotate.sh
ADD Sieve/.dovecot.sieve /etc/dovecot/sieve/.dovecot.sieve
ADD Sieve/spamglobal.sieve /etc/dovecot/sieve/spamglobal.sieve

RUN sievec /etc/dovecot/sieve/.dovecot.sieve && \
    sievec /etc/dovecot/sieve/spamglobal.sieve && \
    groupadd -g 5000 vmail && useradd -g vmail -u 5000 vmail -d /home/vmail -m && \
    chgrp vmail /etc/dovecot/dovecot.conf && chmod g+r /etc/dovecot/dovecot.conf && \
    chgrp -R vmail /etc/dovecot/sieve && chmod -R 750 /etc/dovecot/sieve

EXPOSE $PORT_EXPOSED_IMAPS

CMD sh start.sh "${DB}" "${DB_PORT}"