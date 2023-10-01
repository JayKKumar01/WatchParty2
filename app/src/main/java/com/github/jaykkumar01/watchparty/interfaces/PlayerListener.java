package com.github.jaykkumar01.watchparty.interfaces;

public interface PlayerListener {
    void onSeek(long positionMs);


    void onIsPlaying(boolean isPlaying);

    void onPlayerReady();
}
