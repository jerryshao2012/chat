package com.ibm.kafkachatserver.models;

public class Message {

    // ----------------------------------------------------- Instance Variables
    private String sender;
    private String content;
    private String timestamp;

    // ----------------------------------------------------- Private Methods

    // ----------------------------------------------------- Protected Methods

    // ----------------------------------------------------- Public Methods

    public Message() {
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(final String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(final String sender) {
        this.sender = sender;
    }

    public Message(final String sender, final String content) {
        setSender(sender);
        setContent(content);
    }

    public String getContent() {
        return content;
    }

    public void setContent(final String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Message{sender='%s', content='%s', timestamp='%s'}"
                .formatted(getSender(), getContent(), getTimestamp());
    }
}
