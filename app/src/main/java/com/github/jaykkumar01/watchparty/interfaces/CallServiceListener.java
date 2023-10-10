package com.github.jaykkumar01.watchparty.interfaces;

import com.github.jaykkumar01.watchparty.models.MessageModel;

public interface CallServiceListener {
    void onJoinCall(String id);
    void onToogleMic();
    void onDisconnect();
    void onToogleDeafen();

    void sendMessage(MessageModel messageModel);

    void onSendSeekInfo(long positionMs);

    void onSendPlayPauseInfo(boolean isPlaying);

    void onSendPlaybackState(String id, boolean playing, long currentPosition);

    void onSendPlaybackStateRequest(int i);

    void onActivityStopInfo();

    void onSendJoinedPartyAgain();

    void onSendPlayPauseAndSeekInfo(boolean isPlaying, int currentTime);
}
