package com.github.jaykkumar01.watchparty.interfaces;

import android.content.Context;
import android.content.res.Resources;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.os.Build;
import android.os.Environment;
import android.telecom.Call;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.github.jaykkumar01.watchparty.PlayerActivity;
import com.github.jaykkumar01.watchparty.models.AudioPlayerModel;
import com.github.jaykkumar01.watchparty.models.MessageModel;
import com.github.jaykkumar01.watchparty.models.UserModel;
import com.github.jaykkumar01.watchparty.services.CallService;
import com.github.jaykkumar01.watchparty.update.Info;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JavaScriptInterface implements Data{
    Context context;

    HashMap<String, AudioPlayerModel> playerMap = new HashMap<>();

    ExecutorService executorService = Executors.newSingleThreadExecutor();
    private List<MessageModel> messageModelList = new ArrayList<>();

    public JavaScriptInterface(Context context) {
        this.context = context;
    }

    @JavascriptInterface
    public void onConnected(String id){
//        Toast.makeText(context, "myId: "+id, Toast.LENGTH_SHORT).show();
        CallService.listener.onJoinCall(id);
    }


    @JavascriptInterface
    public void send(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }



    @JavascriptInterface
    public void showFile(String id,byte[] bytes, int read, long millis){
        if (!playerMap.containsKey(id)){
            AudioPlayerModel model = new AudioPlayerModel(id,millis);
            playerMap.put(id,model);
        }

        AudioPlayerModel audioPlayerModel = playerMap.get(id);
        if (audioPlayerModel == null){
            return;
        }


        executorService.execute(new Runnable() {
            @Override
            public void run() {

                long diff = System.currentTimeMillis() - millis - audioPlayerModel.getOffset();
                if (diff > 600){
                    return;
                }

                if (Info.isDeafen){
                    return;
                }
                audioPlayerModel.getAudioTrack().write(bytes,0,read);
            }
        });
    }

    @JavascriptInterface
    public void showMessage(String id, String name, String message, long millis){
        MessageModel messageModel = new MessageModel(id,message);
        messageModel.setName(name);
        messageModel.setTimeMillis(millis);

        PlayerActivity.listener.onReceiveMessage(messageModel);
    }
    @JavascriptInterface
    public void handleSeekInfo(String id,long positionMs){
        PlayerActivity.listener.onSeekInfo(id,positionMs);
    }
    @JavascriptInterface
    public void handlePlayPauseInfo(String id,boolean isPlaying){
        PlayerActivity.listener.onPlayPauseInfo(id,isPlaying);
    }
    @JavascriptInterface
    public void handlePlaybackStateRequest(String id){
        PlayerActivity.listener.onPlaybackStateRequest(id);
    }
    @JavascriptInterface
    public void handlePlaybackState(String id,boolean isPlaying, long positionMs){
        PlayerActivity.listener.onPlaybackStateRecevied(id,isPlaying,positionMs);
    }
}
