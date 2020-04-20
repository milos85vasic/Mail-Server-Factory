FROM centos:centos7.7.1908

RUN yum update -y
RUN yum --enablerepo=centosplus install -y postfix
RUN postconf -c /etc/postfix -m | grep sql
RUN yum install -y openssl
ADD postfix /etc/postfix