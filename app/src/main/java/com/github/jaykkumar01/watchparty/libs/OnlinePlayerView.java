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
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.OptIn;
import androidx.appcompat.app.WindowDecorActionBar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.ui.DefaultTimeBar;
import androidx.media3.ui.TimeBar;

import com.github.jaykkumar01.watchparty.R;
import com.github.jaykkumar01.watchparty.libs.interfaces.YouTubePlayer;
import com.github.jaykkumar01.watchparty.libs.utils.ViewUtil;
import com.github.jaykkumar01.watchparty.utils.Base;
import com.github.jaykkumar01.watchparty.utils.PlayerUtil;
import com.github.jaykkumar01.watchparty.utils.WebTouchGesture;

public class OnlinePlayerView implements View.OnClickListener {

    private long currentSeekBarMillis;
    private boolean isYouTubePlaying;
    private boolean isYouTubeMute;
    private int playbackSpeed = 3;
    private int playbackQuality = 2;

    @Override
    public void onClick(View view) {
        if (view == playPause){
            playAndPause();
        }else if (view == muteUnmute){
            muteUnmute();
        } else if (view == screen){
            screen();
        } else if (view == lock){
            lock();
        } else if (view == unlock) {
            unLock();
        } else if (view == gear){
            changeQuality();
        } else if (view == speed){
            changeSpeed();
        }

    }

    public void changeSpeed() {

        AlertDialog.Builder playbackDialog = new AlertDialog.Builder(context);
        playbackDialog.setTitle("Change Playback Speed");

        final PlayerConstants.PlaybackRate[] rates = PlayerConstants.PlaybackRate.values();
        String[] items = new String[rates.length];
        for (int i = 0; i < rates.length; i++) {
            items[i] = rates[i].toString();
        }

        playbackDialog.setSingleChoiceItems(items, playbackSpeed, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                // Call your YouTubePlayer.setPlaybackRate method here with the chosen playbackRate
                youTubePlayer.setPlaybackRate(rates[playbackSpeed = i].toFloat());

                // Dismiss the dialog after setting the playback rate
                //dialogInterface.dismiss();
            }
        });

        playbackDialog.show();
    }


    public void changeQuality() {

        AlertDialog.Builder qualityDialog = new AlertDialog.Builder(context);
        qualityDialog.setTitle("Change Video Quality");

        final PlayerConstants.PlaybackQuality[] qualities = PlayerConstants.PlaybackQuality.values();
        String[] items = new String[qualities.length]; // Exclude UNKNOWN
        for (int i = 0; i < qualities.length; i++) {
            items[i] = qualities[i].getDisplayName();
        }

        qualityDialog.setSingleChoiceItems(items, playbackQuality, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Set the selected quality to the YouTube player
                youTubePlayer.setPlaybackQuality(qualities[playbackQuality = i].getValue());
                // Dismiss the dialog after setting the quality
            }
        });

        qualityDialog.show();
    }


    private void lock(){
        ctrlLayout.findViewById(R.id.ctrlLayout).setVisibility(View.GONE);
        unlock.setVisibility(View.VISIBLE);
    }
    private void unLock(){
        unlock.setVisibility(View.GONE);
        ctrlLayout.findViewById(R.id.ctrlLayout).setVisibility(View.VISIBLE);
    }

    private void screen() {
        activity.setRequestedOrientation(
                context.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE
                        ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        );
    }

    private void muteUnmute() {
        isYouTubeMute = !isYouTubeMute;
        if (isYouTubeMute){
            youTubePlayer.mute();
            muteUnmute.setImageResource(R.drawable.volume_off);
        }else {
            youTubePlayer.unMute();
            muteUnmute.setImageResource(R.drawable.volume_on);
        }
    }


    private void playAndPause() {
        isYouTubePlaying = !isYouTubePlaying;
        if (isYouTubePlaying){
            playPause.setImageResource(R.drawable.exo_play);
            youTubePlayer.pause();
        }else{
            playPause.setImageResource(R.drawable.exo_pause);
            youTubePlayer.play();
        }
    }

    public interface Listener{
        void onReady();
        void onUpdateCurrentDuration(int currentDuration);
        void onUpdateTotalDuration(int totalDuration);

        void onConfigChange(Configuration newConfig);
    }
    public static Listener listener;

    private final Context context;
    public final Activity activity;
    private final WebView webView;

    private boolean isStarted;

    private final Handler mainThread = new Handler(Looper.getMainLooper());

    private YouTubePlayer youTubePlayer;


    public ImageView playPause,muteUnmute,lock,unlock,screen,gear,speed;
    public TextView currentDurationTV,totalDurationTV;
    public DefaultTimeBar seekBar;
    private final ConstraintLayout ctrlLayout;



    private WebTouchGesture webTouchGesture;
    private final ViewUtil viewUtil;
    private boolean isSeekBarTouched;
    @SuppressLint("ClickableViewAccessibility")
    public OnlinePlayerView(Context context, ConstraintLayout playerLayout) {
        this.context = context;
        activity = (Activity)context;
        viewUtil = new ViewUtil(context);
        webView = new WebView(context);
        viewUtil.initWebView(webView,playerLayout);
        ctrlLayout = viewUtil.attachControlLayout(playerLayout);


        viewUtil.initViews(this);
        listener = setListener();
        webTouchGesture = new WebTouchGesture(context,webView,ctrlLayout);
//        ctrlLayout.setVisibility(View.GONE);
        webView.setOnTouchListener(webTouchGesture);
        setupControlListeners();
        viewUtil.setOnClickListenerToImageViews(ctrlLayout,this);
    }

    @OptIn(markerClass = UnstableApi.class)
    private void  setupControlListeners() {
        seekBar.addListener(new TimeBar.OnScrubListener() {
            @Override
            public void onScrubStart(TimeBar timeBar, long position) {
                isSeekBarTouched = true;

            }

            @Override
            public void onScrubMove(TimeBar timeBar, long position) {

            }

            @Override
            public void onScrubStop(TimeBar timeBar, long position, boolean canceled) {
                currentSeekBarMillis = System.currentTimeMillis();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        youTubePlayer.seekTo(position);
                    }
                });

                mainThread.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (System.currentTimeMillis() - currentSeekBarMillis >= 500) {
                            isSeekBarTouched = false;
                        }
                    }
                },500);
            }
        });
    }

    @OptIn(markerClass = UnstableApi.class)
    private Listener setListener() {
        return new Listener() {
            @Override
            public void onReady() {
                youTubePlayer = new YouTubePlayerImpl(webView);
                youTubePlayer.play();
            }


            @Override
            public void onUpdateCurrentDuration(int seconds) {
                String currentDuration = Base.formatSeconds(seconds);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        currentDurationTV.setText(currentDuration);
                        if (!isSeekBarTouched) {
                            seekBar.setPosition(seconds);
                        }
                    }
                });
            }

            @Override
            public void onUpdateTotalDuration(int seconds) {
                String totalDuration = Base.formatSeconds(seconds);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        totalDurationTV.setText(totalDuration);
                        seekBar.setDuration(seconds);
                    }
                });
            }

            @Override
            public void onConfigChange(Configuration newConfig) {
                if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
                    screen.setImageResource(R.drawable.fullscreen_exit);
                }else{
                    screen.setImageResource(R.drawable.fullscreen);
                }
            }
        };
    }

    public void start(){
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
        //isYouTubePlaying = false;
        //        webPlayPause.setImageResource(R.drawable.exo_pause);
        //        isYouTubeMute = false;
        //        webMuteUnmute.setImageResource(R.drawable.volume_on);
        //        onlinePlayerCurrentDuration.setText(getString(R.string._00_00));
        //        onlinePlayerTotalDuration.setText(getString(R.string._00_00));
    }

    @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled"})
    private void setupWebView() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onPermissionRequest(PermissionRequest request) {
                request.grant(request.getResources());
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                boolean x = !url.equals("file:///android_asset/youtube_iframe_api.html");
                if (x) {
                    return;
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


}
