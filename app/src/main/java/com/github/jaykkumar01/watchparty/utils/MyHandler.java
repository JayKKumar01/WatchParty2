package com.github.jaykkumar01.watchparty.utils;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.media3.ui.PlayerView;

@SuppressLint("UnsafeOptInUsageError")
public class MyHandler {
    private static final long CONTROL_HIDE_TIME = 3500;
    private static final Handler handler = new Handler();
    private static long TOUCH_ACTIVE = System.currentTimeMillis();


    public static void hideControls(PlayerView playerView) {
        TOUCH_ACTIVE = System.currentTimeMillis();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(playerView.isControllerFullyVisible() && (System.currentTimeMillis() - TOUCH_ACTIVE) >= CONTROL_HIDE_TIME){
                    playerView.hideController();
                }
            }
        }, CONTROL_HIDE_TIME);
    }


    public static void hideControls(ConstraintLayout onlinePlayerControlLayout) {
        TOUCH_ACTIVE = System.currentTimeMillis();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(onlinePlayerControlLayout.getVisibility() == View.VISIBLE && (System.currentTimeMillis() - TOUCH_ACTIVE) >= CONTROL_HIDE_TIME){
                    onlinePlayerControlLayout.setVisibility(View.GONE);
                }
            }
        }, CONTROL_HIDE_TIME);
    }
}
