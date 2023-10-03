package com.github.jaykkumar01.watchparty.utils;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

public class TouchGesture implements View.OnTouchListener{
    private final ScaleGestureDetector PINCH_ZOOM;
    private final GestureDetector TAP_TAP;
    Context context;
    PlayerView playerView;
    ExoPlayer player;

    public TouchGesture(Context context, PlayerView playerView, ExoPlayer player) {
        this.context = context;
        this.playerView = playerView;
        this.player = player;
        PINCH_ZOOM = new ScaleGestureDetector(context,new PinchZoom(playerView));
        TAP_TAP = new GestureDetector(context,new TapTap(context,playerView,player));


    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        PINCH_ZOOM.onTouchEvent(event);
        TAP_TAP.onTouchEvent(event);
        return true;
    }
}
