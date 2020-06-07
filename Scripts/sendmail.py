import smtplib, ssl

port = 587
smtp_server = "test1.dev.local"
sender_email = "test1@dev.local"
receiver_email = "test2@dev.local"
password = "Test"
message = "Test message"

context = ssl.create_default_context()
with smtplib.SMTP(smtp_server, port) as server:
    server.ehlo()
#     server.starttls(context=context)
#     server.ehlo()
#     server.login(sender_email, password)
#     server.sendmail(sender_email, receiver_email, message)
