package com.github.jaykkumar01.watchparty.libs.interfaces;

public interface YouTubePlayer {
    void play();
    void pause();
    void mute();
    void unMute();
    void seekTo(long position);
    void setPlaybackRate(float value);
    void setPlaybackQuality(String value);
    void addListener(YouTubePlayerListener playerListener);

    void clean();

    void createPlayer(String link);

    void updatePlaybackState(String id);

    void updatePlayback(boolean isPlaying, long positionMs);
}
