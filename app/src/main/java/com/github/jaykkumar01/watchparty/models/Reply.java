package com.github.jaykkumar01.watchparty.models;

import java.io.Serializable;

public class Reply implements Serializable {
    String message;
    String name;

    public Reply(String message, String name) {
        this.message = message;
        this.name = name;
    }

    public Reply() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
