package com.github.jaykkumar01.watchparty.utils;

import android.annotation.SuppressLint;
import android.view.ScaleGestureDetector;

import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.StyledPlayerView;

public class PinchZoom implements ScaleGestureDetector.OnScaleGestureListener {
    StyledPlayerView playerView;
    private float x = 0f;

    public PinchZoom(StyledPlayerView playerView) {
        this.playerView = playerView;
    }


    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        x = detector.getScaleFactor();
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @SuppressLint("UnsafeOptInUsageError")
    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        if(x>1){
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
        }
        else{
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
        }
    }
}
