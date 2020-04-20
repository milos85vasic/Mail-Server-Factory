FROM centos:centos7.7.1908

RUN yum update -y
RUN yum install -y postfix postfix-postgres openssl
ADD postfix /etc/postfix