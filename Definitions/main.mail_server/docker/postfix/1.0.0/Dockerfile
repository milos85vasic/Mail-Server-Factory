FROM fedora:32

ARG DB_NAME
ARG AV_NAME
ARG DB_PORT
ARG AV_PORT
ARG AV_PORT_MAIL_SEND
ARG SERVICE_RECEIVE

ENV AV_NAME "$AV_NAME"
ENV DB_NAME "$DB_NAME"
ENV DB_PORT "$DB_PORT"
ENV AV_PORT "$AV_PORT"
ENV AV_PORT_MAIL_SEND "$AV_PORT_MAIL_SEND"
ENV SERVICE_RECEIVE "$SERVICE_RECEIVE"

RUN dnf update -y && \
    dnf install findutils -y && \
    dnf clean all && \
    dnf install -y https://download1.rpmfusion.org/free/fedora/rpmfusion-free-release-$(rpm -E %fedora).noarch.rpm \
    https://download1.rpmfusion.org/nonfree/fedora/rpmfusion-nonfree-release-$(rpm -E %fedora).noarch.rpm && \
    dnf install -y postfix postfix-pgsql openssl telnet net-tools iputils && \
    alternatives --set mta /usr/sbin/sendmail.postfix && \
    groupadd -g 5000 vmail && useradd -g vmail -u 5000 vmail -d /home/vmail -m

ADD Configuration/*.* /etc/postfix/
ADD Scripts/start.sh /start.sh
ADD Scripts/logrotate.sh /logrotate.sh

EXPOSE 465

CMD sh start.sh "${DB_PORT}" "${AV_PORT}" "${DB_NAME}" "${AV_NAME}" "${SERVICE_RECEIVE}" "${AV_PORT_MAIL_SEND}"