FROM centos:centos7.7.1908

RUN yum update -y
RUN yum --enablerepo=centosplus install -y postfix
RUN yum install -y openssl
RUN alternatives --set mta /usr/sbin/sendmail.postfix
ADD Configuration /etc/postfix
RUN chgrp postfix /etc/postfix/*

# TODO: Postconf

ADD Scripts/start.sh /start.sh

# TODO: Env
# TODO: Expose

RUN newaliases
# TODO: Use Postgres
RUN postalias /etc/aliases
# TODO: Mapping virtual with system accounts, probably we will need this:
# RUN postmap virtual_alias && postmap canonical
CMD sh start.sh