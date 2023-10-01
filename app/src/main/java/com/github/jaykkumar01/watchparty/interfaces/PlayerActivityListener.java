package com.github.jaykkumar01.watchparty.interfaces;


import com.github.jaykkumar01.watchparty.models.MessageModel;

import java.util.List;

public interface PlayerActivityListener {
    void onReceiveMessage(MessageModel messageModel);

    void onToogleMic();

    void onDisconnect();

    void onToogleDeafen();

    void onSeekInfo(String id, long positionMs);

    void onPlayPauseInfo(String id, boolean isPlaying);

    void onPlaybackStateRequest(String id);

    void onPlaybackStateRecevied(String id, boolean isPlaying, long positionMs);
}
