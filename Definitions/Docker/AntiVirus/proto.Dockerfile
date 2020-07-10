FROM fedora:32

RUN dnf update -y
RUN dnf install -y https://download1.rpmfusion.org/free/fedora/rpmfusion-free-release-$(rpm -E %fedora).noarch.rpm https://download1.rpmfusion.org/nonfree/fedora/rpmfusion-nonfree-release-$(rpm -E %fedora).noarch.rpm
RUN dnf install -y clamd amavis perl-Digest-SHA1 perl-IO-stringy telnet net-tools

ADD Configuration/Clamd /etc/clamd.d
ADD Configuration/Amavisd /etc/amavisd