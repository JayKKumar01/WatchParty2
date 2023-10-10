package com.github.jaykkumar01.watchparty.libs;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.github.jaykkumar01.watchparty.PlayerActivity;
import com.github.jaykkumar01.watchparty.services.CallService;

public class OnlinePlayerBridge {
    private final Context context;
    public OnlinePlayerBridge(Context context) {
        this.context = context;
    }

    @JavascriptInterface
    public void log(String str){
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }

    @JavascriptInterface
    public void onPlayPause(boolean isPlaying){
        OnlinePlayerView.listener.onPlayPause(isPlaying);
    }

    @JavascriptInterface
    public void onPlayerReady(){
        OnlinePlayerView.listener.onPlayerReady();
    }

    @JavascriptInterface
    public void onPlaybackUpdate(String id, boolean isPlaying, long currentPosition){
        // dont use auto detect event, on seek find something
        CallService.listener.onSendPlaybackState(id,isPlaying,currentPosition);
        //Toast.makeText(context, ""+isPlaying+"\n"+currentPosition, Toast.LENGTH_SHORT).show();
    }

    @JavascriptInterface
    public void onReady(){
        OnlinePlayerView.listener.onReady();
    }

}
