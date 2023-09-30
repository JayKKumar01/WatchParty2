package com.github.jaykkumar01.watchparty.interfaces;

import com.github.jaykkumar01.watchparty.models.MessageModel;

public interface CallServiceListener {
    void onJoinCall(String id);
    void onToogleMic();
    void onDisconnect();
    void onToogleDeafen();

    void sendMessage(MessageModel messageModel);
}
