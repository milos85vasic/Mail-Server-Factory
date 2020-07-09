require "fileinto";

if header :contains "subject" "spam" {
        fileinto "Trash";
}