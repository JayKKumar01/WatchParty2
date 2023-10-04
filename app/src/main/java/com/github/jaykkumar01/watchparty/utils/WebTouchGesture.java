package com.github.jaykkumar01.watchparty.utils;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.webkit.WebView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.github.jaykkumar01.watchparty.interfaces.OnlinePlayerListener;

public class WebTouchGesture implements View.OnTouchListener{
    private final ScaleGestureDetector PINCH_ZOOM;
    private final GestureDetector TAP_TAP;
    private final WebPinchZoom pinchZoom;
    Context context;
    WebView webView;
    OnlinePlayerListener listener;
    ConstraintLayout onlinePlayerControlLayout;

    public WebTouchGesture(Context context, WebView webView, OnlinePlayerListener listener, ConstraintLayout onlinePlayerControlLayout) {
        this.context = context;
        this.webView = webView;
        this.listener = listener;
        this.onlinePlayerControlLayout = onlinePlayerControlLayout;
        pinchZoom = new WebPinchZoom(context,webView);
        PINCH_ZOOM = new ScaleGestureDetector(context,pinchZoom);
        TAP_TAP = new GestureDetector(context,new WebTapTap(context,webView,listener,onlinePlayerControlLayout));
    }

    public void resize(){
        pinchZoom.reset();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        PINCH_ZOOM.onTouchEvent(event);
        TAP_TAP.onTouchEvent(event);
        return true;
    }
}
