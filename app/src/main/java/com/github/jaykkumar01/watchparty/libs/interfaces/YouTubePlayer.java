package com.github.jaykkumar01.watchparty.libs.interfaces;

import com.github.jaykkumar01.watchparty.libs.PlayerConstants;

public interface YouTubePlayer {
    void play();
    void pause();
    void mute();
    void unMute();
    void seekTo(long position);
    void setPlaybackRate(float value);
    void setPlaybackQuality(String value);
    void addListener(YouTubePlayerListener playerListener);
}
