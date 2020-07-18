FROM fedora:32

RUN dnf update -y && \
    dnf install findutils -y && \
    dnf clean all && \
    dnf install -y https://download1.rpmfusion.org/free/fedora/rpmfusion-free-release-$(rpm -E %fedora).noarch.rpm https://download1.rpmfusion.org/nonfree/fedora/rpmfusion-nonfree-release-$(rpm -E %fedora).noarch.rpm && \
    dnf install -y clamav clamav-update amavis perl-Digest-SHA1 perl-IO-stringy telnet net-tools iputils

ADD Configuration/Clamd /etc/clamd.d
RUN rm -f /etc/amavisd/amavisd.conf
ADD Configuration/Amavisd /etc
ADD Scripts/do_clam.sh /do_clam.sh
ADD Scripts/start.sh /start.sh
ADD Scripts/logrotate.sh /logrotate.sh

RUN mkdir /target

EXPOSE {{SERVICE.ANTI_VIRUS.PORTS.PORT}}

CMD sh start.sh