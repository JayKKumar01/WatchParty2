package com.github.jaykkumar01.watchparty.libs.utils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.media3.ui.DefaultTimeBar;
import androidx.media3.ui.TimeBar;

import com.github.jaykkumar01.watchparty.R;
import com.github.jaykkumar01.watchparty.libs.OnlinePlayerView;

public class ViewUtil{
    private final Context context;
    private ConstraintLayout ctrlLayout;

    public ViewUtil(Context context) {
        this.context = context;
    }

    public void setOnClickListenerToImageViews(View view, View.OnClickListener listener) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                if (child instanceof ImageView) {
                    ImageView imageView = (ImageView) child;
                    imageView.setOnClickListener(listener);
                }
                if (child instanceof ViewGroup) {
                    setOnClickListenerToImageViews(child, listener);
                }
            }
        }
    }

    public void initViews(OnlinePlayerView onlinePlayerView) {

//        onlinePlayerView.playPause = findView(R.id.play_pause);
//        onlinePlayerView.muteUnmute = findView(R.id.mute_unmute);
//        onlinePlayerView.lock = findView(R.id.lock);
//        onlinePlayerView.unlock = findView(R.id.big_lock);
//        onlinePlayerView.screen = findView(R.id.screen);
//        onlinePlayerView.gear = findView(R.id.vidTrack);
//        onlinePlayerView.speed = findView(R.id.speed);
//        onlinePlayerView.currentDurationTV = findView(R.id.position);
//        onlinePlayerView.totalDurationTV = findView(R.id.duration);
//        onlinePlayerView.seekBar = findView(R.id.progress);

    }

    public ConstraintLayout attachControlLayout(ConstraintLayout onlinePlayerLayout) {
        ConstraintLayout onlinePlayerControlLayout = (ConstraintLayout) LayoutInflater.from(context)
                .inflate(R.layout.custom_controls_online, null);
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
        );
        onlinePlayerControlLayout.setLayoutParams(layoutParams);
        onlinePlayerLayout.addView(onlinePlayerControlLayout);
        return ctrlLayout = onlinePlayerControlLayout;
    }

    public void initWebView(WebView webView, ConstraintLayout playerLayout) {
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
        );
        webView.setLayoutParams(layoutParams);
        playerLayout.addView(webView);
    }


    private <T extends View> T findView(int viewId) {
        View view = ctrlLayout.findViewById(viewId);
        //noinspection unchecked
        return (T) view;
    }

    public void addView(View screen, ConstraintLayout playerLayout) {
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
        );
        screen.setLayoutParams(layoutParams);
        playerLayout.addView(screen);
    }
}

