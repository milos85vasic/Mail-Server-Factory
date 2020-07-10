FROM fedora:32

RUN dnf update -y
RUN dnf install -y https://download1.rpmfusion.org/free/fedora/rpmfusion-free-release-$(rpm -E %fedora).noarch.rpm https://download1.rpmfusion.org/nonfree/fedora/rpmfusion-nonfree-release-$(rpm -E %fedora).noarch.rpm
RUN dnf install -y clamav clamav-update amavis perl-Digest-SHA1 perl-IO-stringy

ADD Configuration/Clamd /etc/clamd.d
ADD Configuration/Amavisd /etc/amavisd
ADD Scripts/do_clam.sh /do_clam.sh
ADD Scripts/start.sh /start.sh

RUN mkdir /target
RUN touch /var/log/clamd.scan
RUN chgrp clamscan /var/log/clamd.scan
RUN chown clamscan /var/log/clamd.scan
RUN chgrp clamscan /etc/clamd.d/scan.conf

CMD sh start.sh