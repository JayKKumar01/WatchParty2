package com.github.jaykkumar01.watchparty.models;

import java.io.Serializable;

public class SampleData implements Serializable {
    String str;
    int val;
    long millis;

    public SampleData() {
    }

    public SampleData(String str, int val, long millis) {
        this.str = str;
        this.val = val;
        this.millis = millis;
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    public int getVal() {
        return val;
    }

    public void setVal(int val) {
        this.val = val;
    }

    public long getMillis() {
        return millis;
    }

    public void setMillis(long millis) {
        this.millis = millis;
    }
}
