package com.github.jaykkumar01.watchparty.libs.models;

import java.io.Serializable;

public class YouTubeData implements Serializable{
    String title;
    String link;

    public YouTubeData() {
    }

    public YouTubeData(String link, String title) {
        this.link = link;
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public YouTubeData(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
