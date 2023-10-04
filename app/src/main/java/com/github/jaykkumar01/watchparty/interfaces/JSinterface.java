package com.github.jaykkumar01.watchparty.interfaces;

import android.content.Context;
import android.webkit.JavascriptInterface;

import com.github.jaykkumar01.watchparty.PlayerActivity;

public class JSinterface {
    private Context context;
    public JSinterface(Context context) {
        this.context = context;
    }

    @JavascriptInterface
    public void updateCurrentDuration(int currentDuration){
        PlayerActivity.onlinePlayerListener.onUpdateCurrentDuration(currentDuration);
    }

    @JavascriptInterface
    public void updateTotalDuration(int totalDuration) {
        PlayerActivity.onlinePlayerListener.onUpdateTotalDuration(totalDuration);
    }
}
