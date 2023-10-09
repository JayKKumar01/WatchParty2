package com.github.jaykkumar01.watchparty.javascriptinterfaces;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.github.jaykkumar01.watchparty.helpers.PeerManagement;
import com.github.jaykkumar01.watchparty.helpers.PlayerManagement;
import com.github.jaykkumar01.watchparty.helpers.RecycleViewManagement;
import com.github.jaykkumar01.watchparty.interfaces.Data;
import com.github.jaykkumar01.watchparty.models.AudioPlayerModel;
import com.github.jaykkumar01.watchparty.models.MessageModel;
import com.github.jaykkumar01.watchparty.services.CallService;
import com.github.jaykkumar01.watchparty.update.Info;
import com.github.jaykkumar01.watchparty.utils.ObjectUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JavaScriptInterface implements Data {
    Context context;


    private final ConcurrentHashMap<String, AudioPlayerModel> playerMap = new ConcurrentHashMap<>();

    ExecutorService executorService = Executors.newSingleThreadExecutor();
    private List<MessageModel> messageModelList = new ArrayList<>();

    public JavaScriptInterface(Context context) {
        this.context = context;
    }

    @JavascriptInterface
    public void onConnected(String id){
        //Toast.makeText(context, "myId: "+id, Toast.LENGTH_SHORT).show();
        CallService.listener.onJoinCall(id);
    }


    @JavascriptInterface
    public void send(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }



    @JavascriptInterface
    public void showFile(String id,byte[] bytes,int read, long millis, float loudness){
        if (Info.isDeafen){
            return;
        }





        playerMap.putIfAbsent(id, new AudioPlayerModel(id, millis));

        AudioPlayerModel audioPlayerModel = playerMap.get(id);

        executorService.execute(() -> {
            audioPlayerModel.processFile(bytes,read,millis,id,loudness);
        });
    }

    @JavascriptInterface
    public void showMessage(String id, byte[] nameBytes, byte[] msgBytes, long millis){
        MessageModel messageModel = new MessageModel(id,ObjectUtil.restoreString(msgBytes));
        messageModel.setName(ObjectUtil.restoreString(nameBytes));
        messageModel.setTimeMillis(millis);

        PeerManagement.listener.onReceiveMessage(messageModel);
        RecycleViewManagement.listener.onReceiveMessage(messageModel);
    }
    @JavascriptInterface
    public void handleJoinedPartyAgain(String id,byte[] nameBytes, long millis){
        Toast.makeText(context, ObjectUtil.restoreString(nameBytes)+" joined the party again!", Toast.LENGTH_SHORT).show();
    }
}
