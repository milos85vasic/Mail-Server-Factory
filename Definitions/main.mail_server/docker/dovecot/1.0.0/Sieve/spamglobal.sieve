require ["fileinto","mailbox"];

if anyof(
    header :contains ["X-Spam-Flag"] "YES",
    header :contains ["X-Spam"] "Yes",
    header :contains ["Subject"] "*** SPAM ***"
    )
{

    fileinto :create "INBOX/Spam";
    stop;
}

if header :contains ["X-Spamd-Result"] "False" {

    fileinto "INBOX";
} else {

    fileinto "INBOX/Unclassified";
    stop;
}