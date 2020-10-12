require ["fileinto","mailbox"];

if header :contains ["X-Virus-Status"] "Infected" {

    fileinto "INBOX/Quarantine";
    stop;
}

if header :contains ["X-Virus-Scanned"] "amavisd-new" {

    fileinto "INBOX";
    stop;
} else {

    fileinto "INBOX/Unscanned";
    stop;
}