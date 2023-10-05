package com.github.jaykkumar01.watchparty.libs;

import android.os.Handler;
import android.os.Looper;
import android.webkit.WebView;

import com.github.jaykkumar01.watchparty.libs.interfaces.YouTubePlayer;
import com.github.jaykkumar01.watchparty.libs.interfaces.YouTubePlayerListener;

public class YouTubePlayerImpl implements YouTubePlayer {
    private final WebView webView;
    private final Handler mainThread = new Handler(Looper.getMainLooper());
    public YouTubePlayerImpl(WebView webView) {
        this.webView = webView;
    }

    @Override
    public void play() {
        invoke("playPauseVideo",true);
    }

    @Override
    public void pause() {
        invoke("playPauseVideo",false);
    }

    @Override
    public void mute() {
        invoke("setPlayerMute", true);
    }

    @Override
    public void unMute() {
        invoke("setPlayerMute", false);
    }

    @Override
    public void seekTo(long position) {
        invoke("setCurrentDuration", position);
    }

    @Override
    public void setPlaybackRate(float rate) {
        invoke("setPlaybackRate", rate);
    }

    @Override
    public void setPlaybackQuality(String quality) {
        invoke("enableControlsForDuration",10);
    }

    @Override
    public void addListener(YouTubePlayerListener playerListener) {

    }

    private void invoke(String function, Object... args) {
        StringBuilder argString = new StringBuilder();
        for (Object arg : args) {
            if (arg instanceof String) {
                argString.append("'").append(arg).append("'");
            } else {
                argString.append(arg.toString());
            }
            argString.append(",");
        }
        if (argString.length() > 0) {
            argString.deleteCharAt(argString.length() - 1); // Remove the trailing comma
        }

        final String javascriptCommand = String.format("javascript:%s(%s)", function, argString.toString());

        mainThread.post(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl(javascriptCommand);
            }
        });
    }

}
