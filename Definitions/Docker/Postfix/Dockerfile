FROM centos:centos7.7.1908

RUN yum update -y
RUN yum --enablerepo=centosplus install -y postfix
RUN yum install -y openssl telnet
RUN alternatives --set mta /usr/sbin/sendmail.postfix

ADD Configuration/*.* /etc/postfix/
ADD Scripts/start.sh /start.sh

RUN groupadd -g 5000 vmail && useradd -g vmail -u 5000 vmail -d /home/vmail -m

EXPOSE 465
EXPOSE 587

CMD sh start.sh