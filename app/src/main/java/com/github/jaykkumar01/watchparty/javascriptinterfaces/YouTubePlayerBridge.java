package com.github.jaykkumar01.watchparty.javascriptinterfaces;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.github.jaykkumar01.watchparty.enums.PlayerType;
import com.github.jaykkumar01.watchparty.helpers.PlayerManagement;
import com.github.jaykkumar01.watchparty.interfaces.Data;
import com.github.jaykkumar01.watchparty.libs.OnlinePlayerView;
import com.github.jaykkumar01.watchparty.utils.ObjectUtil;

public class YouTubePlayerBridge implements Data {
    Context context;

    public YouTubePlayerBridge(Context context) {
        this.context = context;
    }

    @JavascriptInterface
    public void handleActivityStop(String id,byte[] nameBytes, long millis){
        //PlayerManagement.listener.onPlayPauseInfo(id,false);
//        Toast.makeText(context, ObjectUtil.restoreString(nameBytes)+" left the party!", Toast.LENGTH_SHORT).show();
    }
    @JavascriptInterface
    public void handleSeekInfo(String id,long positionMs){
        //PlayerManagement.listener.onSeekInfo(id,positionMs);
    }
    @JavascriptInterface
    public void handlePlayPauseInfo(String id,boolean isPlaying){
        //PlayerManagement.listener.onPlayPauseInfo(id,isPlaying);
    }
    @JavascriptInterface
    public void handlePlaybackStateRequest(String id){
        OnlinePlayerView.listener.onPlaybackStateRequest(id);
    }
    @JavascriptInterface
    public void handlePlaybackState(String id,boolean isPlaying, long positionMs){
        //PlayerManagement.listener.onPlaybackStateReceived(id,isPlaying,positionMs);
    }
}
