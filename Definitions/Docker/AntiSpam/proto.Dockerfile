FROM centos:8

RUN dnf update -y && \
    dnf install -y findutils && \
    dnf clean all && \
    dnf install -y --nogpgcheck https://dl.fedoraproject.org/pub/epel/epel-release-latest-8.noarch.rpm && \
    dnf install -y --nogpgcheck https://download1.rpmfusion.org/free/el/rpmfusion-free-release-8.noarch.rpm https://download1.rpmfusion.org/nonfree/el/rpmfusion-nonfree-release-8.noarch.rpm && \
    dnf install -y python3 libpcap && \
    dnf install -y git cmake make gcc-c++ boost ragel libnet curl telnet net-tools iputils && \
    dnf groupinstall -y "Development Tools" && \
    cd /opt; git clone http://luajit.org/git/luajit-2.0.git; cd luajit-2.0; make && make install; cd / && \
    rpm -Uvh http://repo.openfusion.net/centos7-x86_64/openfusion-release-0.7-1.of.el7.noarch.rpm && \
    dnf install -y hyperscan hyperscan-devel && \
    curl https://rspamd.com/rpm-stable/centos-8/rspamd.repo > /etc/yum.repos.d/rspamd.repo && \
    rpm --import https://rspamd.com/rpm-stable/gpg.key && \
    dnf install -y rspamd

ADD Scripts/start.sh /start.sh
ADD Scripts/logrotate.sh /logrotate.sh
ADD Configuration/worker-proxy.inc /etc/rspamd/local.d/worker-proxy.inc
ADD Configuration/worker-normal.inc /etc/rspamd/local.d/worker-normal.inc
ADD Configuration/classifier-bayes.conf /etc/rspamd/local.d/classifier-bayes.conf
ADD Configuration/milter_headers.conf /etc/rspamd/local.d/milter_headers.conf
ADD Configuration/worker-controller.inc /etc/rspamd/local.d/worker-controller.inc
ADD Configuration/logging.inc /etc/rspamd/local.d/logging.inc
ADD Configuration/dkim_signing.conf /etc/rspamd/local.d/dkim_signing.conf

RUN cp /etc/rspamd/local.d/dkim_signing.conf /etc/rspamd/local.d/arc.conf
RUN mkdir /var/run/rspamd
RUN printf "password = \"$(rspamadm pw --encrypt -p {{SERVICE.ANTI_SPAM.WEBUI.PASSWORD}})\";\n" >> /etc/rspamd/local.d/worker-controller.inc
RUN printf "enable_password = \"$(rspamadm pw --encrypt -p {{SERVICE.ANTI_SPAM.WEBUI.PASSWORD}})\";\n" >> /etc/rspamd/local.d/worker-controller.inc

EXPOSE {{SERVICE.ANTI_SPAM.PORTS.PROXY}}
EXPOSE {{SERVICE.ANTI_SPAM.PORTS.WORKER}}
EXPOSE {{SERVICE.ANTI_SPAM.PORTS.WEBUI}}

CMD sh start.sh