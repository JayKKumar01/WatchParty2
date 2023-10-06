package com.github.jaykkumar01.watchparty.models;

import org.apache.commons.lang3.StringEscapeUtils;

import java.io.Serializable;

public class MessageModel implements Serializable {
    String userId;
    String name;
    String message;
    long timeMillis;

    Reply reply;

    public ImageModel getImageModel() {
        return imageModel;
    }

    public void setImageModel(ImageModel imageModel) {
        this.imageModel = imageModel;
    }

    ImageModel imageModel;

    public MessageModel() {
    }

    public MessageModel(String userId, String message) {
        this.userId = userId;
        this.message = message;
    }

    public MessageModel(String userId,String name, String message) {
        this.userId = userId;
        this.name = name;
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
//        return StringEscapeUtils.unescapeJava(message);
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
