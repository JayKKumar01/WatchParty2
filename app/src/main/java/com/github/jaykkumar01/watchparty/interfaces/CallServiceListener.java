package com.github.jaykkumar01.watchparty.interfaces;

import com.github.jaykkumar01.watchparty.models.MessageModel;
import com.github.jaykkumar01.watchparty.models.UserModel;

import java.util.List;

public interface CallServiceListener {
    void onJoinCall(String id);
    void onToogleMic();
    void onDisconnect();
    void onToogleDeafen();

    void sendMessage(MessageModel messageModel);
    void receiveMessage(MessageModel messageModel);
}
