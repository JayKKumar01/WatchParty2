package com.github.jaykkumar01.watchparty.libs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.OptIn;
import androidx.appcompat.app.WindowDecorActionBar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.ui.DefaultTimeBar;
import androidx.media3.ui.TimeBar;

import com.github.jaykkumar01.watchparty.PlayerActivity;
import com.github.jaykkumar01.watchparty.R;
import com.github.jaykkumar01.watchparty.libs.interfaces.YouTubePlayer;
import com.github.jaykkumar01.watchparty.libs.utils.ViewUtil;
import com.github.jaykkumar01.watchparty.utils.Base;
import com.github.jaykkumar01.watchparty.utils.PlayerUtil;
import com.github.jaykkumar01.watchparty.utils.WebTouchGesture;

public class OnlinePlayerView{


    private void screen(boolean setLandscape) {
        if (setLandscape){
            activity.setRequestedOrientation(
                    ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            );
        }else{
            activity.setRequestedOrientation(
                    ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            );
        }
//        activity.setRequestedOrientation(
//                context.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE
//                        ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
//        );
    }

    public interface Listener{
        void onReady();

        void onConfigChange(Configuration newConfig);
    }
    public static Listener listener;

    private final Context context;
    public final Activity activity;
    private WebView webView;

    private boolean isStarted;

    private final Handler mainThread = new Handler(Looper.getMainLooper());

    private YouTubePlayer youTubePlayer;
    private String link;
    private final ConstraintLayout playerLayout;



    private WebTouchGesture webTouchGesture;
    private final ViewUtil viewUtil;
    private boolean isSeekBarTouched;
    @SuppressLint("ClickableViewAccessibility")
    public OnlinePlayerView(Context context, ConstraintLayout playerLayout) {
        this.context = context;
        activity = (Activity)context;
        this.playerLayout = playerLayout;
        viewUtil = new ViewUtil(context);


        viewUtil.initViews(this);
        listener = setListener();
//        webTouchGesture = new WebTouchGesture(context,webView,ctrlLayout);
//        webView.setOnTouchListener(webTouchGesture);
    }

    @OptIn(markerClass = UnstableApi.class)
    private Listener setListener() {
        return new Listener() {
            @Override
            public void onReady() {
                youTubePlayer = new YouTubePlayerImpl(webView);
                youTubePlayer.createPlayer(link);
            }

            @Override
            public void onConfigChange(Configuration newConfig) {
                if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
                }else{
                    //activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                }
            }
        };
    }

    public void start(String link){
        this.link = link;
        isStarted = true;
        mainThread.post(this::setupWebView);
    }
    public void stop(){
        if (!isStarted){
            return;
        }
        isStarted = false;
        mainThread.post(this::releaseWebView);
    }

    private void releaseWebView() {
        webView.loadUrl("");
        webView.destroy();
        playerLayout.removeView(webView);
        youTubePlayer = null;
        link = null;
        //isYouTubePlaying = false;
        //        webPlayPause.setImageResource(R.drawable.exo_pause);
        //        isYouTubeMute = false;
        //        webMuteUnmute.setImageResource(R.drawable.volume_on);
        //        onlinePlayerCurrentDuration.setText(getString(R.string._00_00));
        //        onlinePlayerTotalDuration.setText(getString(R.string._00_00));
    }

    @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled"})
    private void setupWebView() {
        webView = new WebView(context);
        viewUtil.initWebView(webView,playerLayout);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebChromeClient(new MyChrome());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                boolean x = !url.equals("file:///android_asset/youtube_iframe_api.html");
                if (x) {

                }
            }

        });
        webView.addJavascriptInterface(new OnlinePlayerBridge(context), "Android");

        WebSettings webSettings = webView.getSettings();
        webSettings.setDomStorageEnabled(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false);

        String path = "file:android_asset/youtube_iframe_api.html";
        webView.loadUrl(path);


    }


    private class MyChrome extends WebChromeClient {
        View screen = null;

        @Override
        public void onPermissionRequest(PermissionRequest request) {
            request.grant(request.getResources());
        }

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            screen(true);
            webView.setVisibility(View.GONE);
            screen = view;
            playerLayout.removeView(webView);
            viewUtil.addView(screen,playerLayout);
        }

        @Override
        public void onHideCustomView() {
            screen(false);
            playerLayout.removeView(screen);
            viewUtil.initWebView(webView,playerLayout);
            webView.setVisibility(View.VISIBLE);

            youTubePlayer.clean();

        }

    }
}