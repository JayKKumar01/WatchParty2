package com.github.jaykkumar01.watchparty.libs.models;

public class YouTubeApiInfo {
    private String title;
    private String author_name;
    private String author_url;
    private String type;
    private int height;
    private int width;
    private String version;
    private String provider_name;
    private String provider_url;
    private int thumbnail_height;
    private int thumbnail_width;
    private String thumbnail_url;
    private String html;

    // Getters and setters for the fields (you can generate these using your IDE)

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor_name() {
        return author_name;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }

    public String getAuthor_url() {
        return author_url;
    }

    public void setAuthor_url(String author_url) {
        this.author_url = author_url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getProvider_name() {
        return provider_name;
    }

    public void setProvider_name(String provider_name) {
        this.provider_name = provider_name;
    }

    public String getProvider_url() {
        return provider_url;
    }

    public void setProvider_url(String provider_url) {
        this.provider_url = provider_url;
    }

    public int getThumbnail_height() {
        return thumbnail_height;
    }

    public void setThumbnail_height(int thumbnail_height) {
        this.thumbnail_height = thumbnail_height;
    }

    public int getThumbnail_width() {
        return thumbnail_width;
    }

    public void setThumbnail_width(int thumbnail_width) {
        this.thumbnail_width = thumbnail_width;
    }

    public String getThumbnail_url() {
        return thumbnail_url;
    }

    public void setThumbnail_url(String thumbnail_url) {
        this.thumbnail_url = thumbnail_url;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    @Override
    public String toString() {
        return "YouTubeApiInfo{" +
                "title='" + title + '\'' +
                ", author_name='" + author_name + '\'' +
                ", author_url='" + author_url + '\'' +
                ", type='" + type + '\'' +
                ", height=" + height +
                ", width=" + width +
                ", version='" + version + '\'' +
                ", provider_name='" + provider_name + '\'' +
                ", provider_url='" + provider_url + '\'' +
                ", thumbnail_height=" + thumbnail_height +
                ", thumbnail_width=" + thumbnail_width +
                ", thumbnail_url='" + thumbnail_url + '\'' +
                ", html='" + html + '\'' +
                '}';
    }
}

