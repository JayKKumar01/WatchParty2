package com.github.jaykkumar01.watchparty.utils;

import android.annotation.SuppressLint;
import android.view.ScaleGestureDetector;

import androidx.media3.ui.AspectRatioFrameLayout;
import androidx.media3.ui.PlayerView;

public class PinchZoom implements ScaleGestureDetector.OnScaleGestureListener {
    PlayerView playerView;
    private float x = 0f;

    public PinchZoom(PlayerView playerView) {
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
