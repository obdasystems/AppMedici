package com.obdasystems.pocmedici.message.model;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class OutMessage {

    private Long date;

    private String text;

    private String subject;

    private Boolean adverseEvent = false;

    private String sender;

    private String recipient;

    private List<Attachment> attachments;

    public OutMessage() {
        attachments = new LinkedList<>();
    }

    public OutMessage(Long dat, String txt, String subj, boolean adverse, String send, String recip) {
        this.date = dat;
        this.text = txt;
        this.subject = subj;
        this.adverseEvent = adverse;
        this.sender = send;
        this.recipient = recip;
        attachments = new LinkedList<>();
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }


    public Boolean getAdverseEvent() {
        return adverseEvent;
    }

    public void setAdverseEvent(Boolean adverseEvent) {
        this.adverseEvent = adverseEvent;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public void addAttachment(Attachment attach) {
        this.attachments.add(attach);
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

}
