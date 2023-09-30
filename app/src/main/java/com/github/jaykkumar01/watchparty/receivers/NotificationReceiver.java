package com.github.jaykkumar01.watchparty.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telecom.Call;

import com.github.jaykkumar01.watchparty.MainActivity;
import com.github.jaykkumar01.watchparty.PlayerActivity;
import com.github.jaykkumar01.watchparty.interfaces.Data;
import com.github.jaykkumar01.watchparty.services.CallService;
import com.github.jaykkumar01.watchparty.update.Info;

public class NotificationReceiver extends BroadcastReceiver implements Data {

    @Override
    public void onReceive(Context context, Intent intent) {
        int requestCode = intent.getIntExtra("requestCode", -1);

        if (requestCode == REQUEST_CODE_MUTE) {
            Info.isMute = !Info.isMute;
            if (CallService.listener != null) {
                CallService.listener.onToogleMic();
            }
            if (PlayerActivity.listener != null) {
                PlayerActivity.listener.onToogleMic();
            }
        } else if (requestCode == REQUEST_CODE_HANGUP) {
            if (CallService.listener != null) {
                CallService.listener.onDisconnect();
            }
            if (PlayerActivity.listener != null) {
                PlayerActivity.listener.onDisconnect();
            }

        } else if (requestCode == REQUEST_CODE_DEAFEN) {
            Info.isDeafen = !Info.isDeafen;
            if (CallService.listener != null) {
                CallService.listener.onToogleDeafen();
            }
            if (PlayerActivity.listener != null) {
                PlayerActivity.listener.onToogleDeafen();
            }

        }
    }
}