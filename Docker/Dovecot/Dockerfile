FROM centos:centos7.7.1908

RUN yum update -y
RUN yum --enablerepo=centosplus install -y dovecot
RUN yum install -y openssl rsyslog
ADD Configuration /etc/dovecot

ADD Scripts/start.sh /start.sh

# TODO: Env
# TODO: Expose

CMD sh start.sh