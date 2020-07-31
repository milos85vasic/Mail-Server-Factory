FROM fedora:8

RUN dnf update -y && \
    dnf install findutils -y && \
    dnf clean all && \
    dnf install -y --nogpgcheck https://dl.fedoraproject.org/pub/epel/epel-release-latest-8.noarch.rpm && \
    dnf install -y --nogpgcheck https://download1.rpmfusion.org/free/el/rpmfusion-free-release-8.noarch.rpm https://download1.rpmfusion.org/nonfree/el/rpmfusion-nonfree-release-8.noarch.rpm && \
    dnf install -y git cmake make gcc-c++ boost ragel python libpcap libpcap-devel libnet curl telnet net-tools iputils && \
    dnf groupinstall -y "Development Tools" && \
    cd /opt; git clone http://luajit.org/git/luajit-2.0.git; cd luajit-2.0; make && make install; cd / && \
    # TODO:
    curl https://rspamd.com/rpm-stable/centos-8/rspamd.repo > /etc/yum.repos.d/rspamd.repo && \
    rpm --import https://rspamd.com/rpm-stable/gpg.key && \
    #    dnf install -y rspamd

# TODO:
#ADD Configuration/Clamd /etc/clamd.d
#ADD Scripts/do_clam.sh /do_clam.sh
ADD Scripts/start.sh /start.sh
#ADD Scripts/logrotate.sh /logrotate.sh

EXPOSE {{SERVICE.ANTI_SPAM.PORTS.PORT}}

CMD sh start.sh