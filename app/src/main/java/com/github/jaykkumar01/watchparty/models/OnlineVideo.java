package com.github.jaykkumar01.watchparty.models;

import com.github.jaykkumar01.watchparty.libs.models.YouTubeData;

public class OnlineVideo {
    private String userId;
    private String name;
    private String youtubeUrl;

    private YouTubeData youTubeData;

    public OnlineVideo() {
        // Empty constructor required by Firebase
    }


    public OnlineVideo(String userId, String name, String youtubeUrl) {
        this.userId = userId;
        this.name = name;
        this.youtubeUrl = youtubeUrl;
    }

    public YouTubeData getYouTubeData() {
        return youTubeData;
    }

    public void setYouTubeData(YouTubeData youTubeData) {
        this.youTubeData = youTubeData;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getYoutubeUrl() {
        return youtubeUrl;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setYoutubeUrl(String youtubeUrl) {
        this.youtubeUrl = youtubeUrl;
    }
}

