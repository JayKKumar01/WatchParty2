package com.github.jaykkumar01.watchparty.interfaces;

import com.github.jaykkumar01.watchparty.models.MessageModel;

public interface MessageListener {
    void onReceiveMessage(MessageModel messageModel);
}
