package com.github.jaykkumar01.watchparty.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.ScaleGestureDetector;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.media3.ui.AspectRatioFrameLayout;

import com.github.jaykkumar01.watchparty.MainActivity;
import com.github.jaykkumar01.watchparty.PlayerActivity;

public class WebPinchZoom implements ScaleGestureDetector.OnScaleGestureListener {
    private final WebView webView;
    private float scaleFactor = 1.0f;
    private float x;

    public WebPinchZoom(Context context, WebView webView) {
        this.webView = webView;
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        x = detector.getScaleFactor();
//        scaleFactor *= detector.getScaleFactor();
//
//        scaleFactor = Math.max(1f,Math.min(scaleFactor,5.0f));
//
//        webView.setScaleX(scaleFactor);
//        webView.setScaleY(scaleFactor);

        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @SuppressLint("UnsafeOptInUsageError")
    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        float f = x > 1 ? Base.getZoomFactor(1.22f) : 1f;
        webView.setScaleY(f);
        webView.setScaleX(f);
    }
    public void reset(){
        scaleFactor = 1f;
        webView.setScaleY(1);
        webView.setScaleX(1);
    }
}
