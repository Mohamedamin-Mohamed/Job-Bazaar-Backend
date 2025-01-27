package com.JobBazaar.Backend.Dto;

public class MailjetDto {
    private String recipientEmail;
    private String recipientName;
    private String subject;

    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setBodyContent(String bodyContent) {
        this.bodyContent = bodyContent;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public String getSubject() {
        return subject;
    }

    public String getBodyContent() {
        return bodyContent;
    }

    private String bodyContent;
}
