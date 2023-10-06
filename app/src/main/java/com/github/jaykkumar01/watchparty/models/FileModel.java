package com.github.jaykkumar01.watchparty.models;

import java.io.Serializable;

public class FileModel implements Serializable {
    byte[] bytes;
    int read;
    long millis;

    public FileModel() {
    }

    public FileModel(byte[] bytes, int read, long millis) {
        this.bytes = bytes;
        this.read = read;
        this.millis = millis;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public int getRead() {
        return read;
    }

    public void setRead(int read) {
        this.read = read;
    }

    public long getMillis() {
        return millis;
    }

    public void setMillis(long millis) {
        this.millis = millis;
    }
}
