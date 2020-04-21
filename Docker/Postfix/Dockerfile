FROM centos:centos7.7.1908

RUN yum update -y
RUN yum --enablerepo=centosplus install -y postfix
RUN yum install -y openssl
ADD Configuration /etc/postfix
RUN chgrp postfix /etc/postfix/*

# TODO: Postconf

ADD Scripts/start.sh /start.sh

# TODO: Env
# TODO: Expose

CMD sh start.sh