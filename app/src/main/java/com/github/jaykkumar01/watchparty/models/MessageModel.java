package com.github.jaykkumar01.watchparty.models;

import javax.xml.transform.sax.SAXResult;

public class MessageModel {
    String userId;
    String name;
    String message;
    long timeMillis;

    Reply reply;

    public MessageModel() {
    }

    public MessageModel(String userId, String message) {
        this.userId = userId;
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Reply getReply() {
        return reply;
    }

    public void setReply(Reply reply) {
        this.reply = reply;
    }

    public long getTimeMillis() {
        return timeMillis;
    }

    public void setTimeMillis(long timeMillis) {
        this.timeMillis = timeMillis;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}