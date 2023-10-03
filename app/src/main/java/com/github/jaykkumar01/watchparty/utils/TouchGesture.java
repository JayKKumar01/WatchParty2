package com.github.jaykkumar01.watchparty.utils;

import android.content.Context;
import android.content.res.Resources;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ui.StyledPlayerView;

public class TouchGesture implements View.OnTouchListener{
    private final ScaleGestureDetector PINCH_ZOOM;
    private final GestureDetector TAP_TAP;
    Context context;
    StyledPlayerView playerView;
    ExoPlayer player;

    public TouchGesture(Context context, StyledPlayerView playerView, ExoPlayer player) {
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
