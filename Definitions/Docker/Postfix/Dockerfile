FROM centos:centos7.7.1908

RUN yum update -y
RUN yum --enablerepo=centosplus install -y postfix
RUN yum install -y openssl
RUN alternatives --set mta /usr/sbin/sendmail.postfix
ADD Configuration/*.* /etc/postfix/

ADD Scripts/start.sh /start.sh

EXPOSE 25
EXPOSE 587

CMD sh start.sh