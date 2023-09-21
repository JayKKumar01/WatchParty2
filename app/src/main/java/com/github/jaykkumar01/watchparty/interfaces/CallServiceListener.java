package com.github.jaykkumar01.watchparty.interfaces;

import com.github.jaykkumar01.watchparty.models.UserModel;

import java.util.List;

public interface CallServiceListener {
    void onJoinCall(boolean join, List<UserModel> userList, long joinTime);
    void onToogleMic();
    void onDisconnect();
    void onToogleDeafen();
}
