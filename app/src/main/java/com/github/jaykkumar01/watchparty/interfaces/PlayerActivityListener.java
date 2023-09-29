package com.github.jaykkumar01.watchparty.interfaces;


import com.github.jaykkumar01.watchparty.models.MessageModel;

import java.util.List;

public interface PlayerActivityListener {
    void onReceiveMessage(List<MessageModel> chatList);
}