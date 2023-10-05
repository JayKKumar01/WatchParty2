package com.github.jaykkumar01.watchparty.libs;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.github.jaykkumar01.watchparty.PlayerActivity;

public class OnlinePlayerBridge {
    private final Context context;
    public OnlinePlayerBridge(Context context) {
        this.context = context;
    }

    @JavascriptInterface
    public void onReady(){
        OnlinePlayerView.listener.onReady();
    }

    @JavascriptInterface
    public void updateCurrentDuration(int currentDuration){
        OnlinePlayerView.listener.onUpdateCurrentDuration(currentDuration);
    }

    @JavascriptInterface
    public void updateTotalDuration(int totalDuration) {
        OnlinePlayerView.listener.onUpdateTotalDuration(totalDuration);
    }
    @JavascriptInterface
    public void sendVideoQuality(String str){
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }
}
