package com.github.jaykkumar01.watchparty;

import static androidx.media3.common.Player.REPEAT_MODE_ONE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PictureInPictureParams;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Rational;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackParameters;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector;
import androidx.media3.exoplayer.trackselection.TrackSelector;
import androidx.media3.ui.DefaultTimeBar;
import androidx.media3.ui.PlayerView;
import androidx.media3.ui.TimeBar;

import com.github.jaykkumar01.watchparty.assets.TrackSelectionDialog;
import com.github.jaykkumar01.watchparty.enums.RoomType;
import com.github.jaykkumar01.watchparty.helpers.HandleEventListener;
import com.github.jaykkumar01.watchparty.helpers.PeerManagement;
import com.github.jaykkumar01.watchparty.helpers.RecycleViewManagement;
import com.github.jaykkumar01.watchparty.interfaces.FirebaseListener;
import com.github.jaykkumar01.watchparty.interfaces.JSinterface;
import com.github.jaykkumar01.watchparty.interfaces.OnlinePlayerListener;
import com.github.jaykkumar01.watchparty.interfaces.PlayerActivityListener;
import com.github.jaykkumar01.watchparty.interfaces.PlayerListener;
import com.github.jaykkumar01.watchparty.models.EventListenerData;
import com.github.jaykkumar01.watchparty.models.ListenerData;
import com.github.jaykkumar01.watchparty.models.MessageModel;
import com.github.jaykkumar01.watchparty.models.OnlineVideo;
import com.github.jaykkumar01.watchparty.models.Room;
import com.github.jaykkumar01.watchparty.models.UserModel;
import com.github.jaykkumar01.watchparty.services.CallService;
import com.github.jaykkumar01.watchparty.update.Info;
import com.github.jaykkumar01.watchparty.utils.AspectRatio;
import com.github.jaykkumar01.watchparty.utils.AutoRotate;
import com.github.jaykkumar01.watchparty.utils.Base;
import com.github.jaykkumar01.watchparty.utils.ChatUtil;
import com.github.jaykkumar01.watchparty.utils.FirebaseUtils;
import com.github.jaykkumar01.watchparty.utils.PickerUtil;
import com.github.jaykkumar01.watchparty.utils.PlayerUtil;
import com.github.jaykkumar01.watchparty.utils.TouchGesture;
import com.github.jaykkumar01.watchparty.utils.WebTouchGesture;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@OptIn(markerClass = UnstableApi.class)
public class PlayerActivity extends AppCompatActivity {
    Room room;
    TextView codeTV,userNameTV;





    public static PlayerActivityListener listener;
    public static OnlinePlayerListener onlinePlayerListener;
    ImageView micBtn,deafenBtn;

    int peerCount;

    PlayerView playerView;
    ExoPlayer player;
    PlayerUtil playerUtil;

    ConstraintLayout layout1,partyLayout,playerLayout;
    TextView offlineAdd,onlineAdd;
    ConstraintLayout offlineAddLayout, onlineAddLayout;
    Uri videoUri;
    TextView currentMediaTV,playOffileVideo;
    ConstraintLayout addMediaLayout;
    private boolean isListenerCommand;
    ImageView playPause,webPlayPause,webMuteUnmute,webFullScreen;
    private boolean isFirstSync;
    private int count;
    private boolean isPartyStopped;
    private boolean pip;
    private int playbackSpeed = 2;
    private ImageView muteUnmute;
    private ImageView imgCC;
    private TrackSelector trackSelector;
    private UserModel userModel;
    private ChatUtil chatUtil;
    private ImageView imgChat;
    private ImageView liveClose;

    private boolean isShowingTrackSelectionDialog;
    private TextView currentOnlineVideoTxt;
    private TextInputEditText youtubeUrlET;
    private AppCompatButton joinYouTube,createYouTube;
    private ConstraintLayout exoplayerLayout,onlinePlayerLayout,allPlayerLayout;
    private WebView webView;
    private JSinterface jsInterface;
    private boolean isYouTubePlaying;
    private TextView onlinePlayerCurrentDuration,onlinePlayerTotalDuration;
    private DefaultTimeBar onlinePlayerSeekBar;
    private boolean isSeekBarTouched;
    private Handler seekBarHandler = new Handler();
    private long currentSeekBarMillis;
    private boolean isYouTubeMute;
    private int totalDuration;
    private ConstraintLayout onlinePlayerControlLayout;
    private WebTouchGesture webTouchGesture;
    private boolean webpip;


    private RecycleViewManagement recycleViewManagement;
    private PeerManagement peerManagement;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_player);
        room = (Room) getIntent().getSerializableExtra(getString(R.string.room));
        if (room == null){
            return;
        }
        userModel = room.getUser();
        AutoRotate.set(this);


        recycleViewManagement = new RecycleViewManagement(this,room);
        peerManagement = new PeerManagement(this,room);





        setUpListener();

        layout1 = findViewById(R.id.LYOUT1);
        partyLayout = findViewById(R.id.partyLayout);
        playerLayout = findViewById(R.id.playerLayout);
        chatUtil = new ChatUtil(this);
        chatUtil.setPlayerLayout(playerLayout);
        chatUtil.setPartyLayout(partyLayout);
        chatUtil.setParentLayout((ConstraintLayout) findViewById(R.id.rootLayout));
        imgChat = findViewById(R.id.exo_chat);
        chatUtil.setImgChat(imgChat);
        liveClose = findViewById(R.id.live_close);
        chatUtil.setLiveClose(liveClose);

        offlineAdd = findViewById(R.id.offlineBtn);
        onlineAdd = findViewById(R.id.onlineBtn);
        offlineAddLayout = findViewById(R.id.offlineMediaLayout);
        onlineAddLayout = findViewById(R.id.onlineMediaLayout);
        exoplayerLayout = findViewById(R.id.exo_player_view);
        onlinePlayerLayout = findViewById(R.id.online_player_view);
        allPlayerLayout = findViewById(R.id.all_player_view);

        attachOnlinePlayerControlLayout();
        webPlayPause = onlinePlayerLayout.findViewById(R.id.play_pause);
        webMuteUnmute = onlinePlayerLayout.findViewById(R.id.mute_unmute);
        webFullScreen = onlinePlayerLayout.findViewById(R.id.screen);
        onlinePlayerCurrentDuration = onlinePlayerLayout.findViewById(R.id.position);
        onlinePlayerTotalDuration = onlinePlayerLayout.findViewById(R.id.duration);
        onlinePlayerSeekBar = onlinePlayerLayout.findViewById(R.id.progress);

        onlinePlayerSeekBar.addListener(new TimeBar.OnScrubListener() {
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callJavaScript("javascript:setCurrentDuration(" + position + ")");
                    }
                });
                seekBarHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (System.currentTimeMillis() - currentSeekBarMillis >= 500) {
                            isSeekBarTouched = false;
                        }
                    }
                },500);
            }
        });



        //setupWebView();

        currentMediaTV = findViewById(R.id.currentMediaTxt);
        playOffileVideo = findViewById(R.id.playOffileVideo);

        currentOnlineVideoTxt = findViewById(R.id.currentOnlineVideoTxt);
        youtubeUrlET = findViewById(R.id.youtubeUrlET);
        joinYouTube = findViewById(R.id.btnJoinYouTube);
        createYouTube = findViewById(R.id.btnCreateYouTube);

        setUpOnlineVideo();


        addMediaLayout = findViewById(R.id.addMediaLayout);
        playerView = findViewById(R.id.player_view);
        trackSelector = new DefaultTrackSelector(this);
        playPause = findViewById(R.id.play_pause);
        muteUnmute = findViewById(R.id.exo_mute_unmute);
        imgCC = findViewById(R.id.exo_caption);

        codeTV = findViewById(R.id.roomCode);
        userNameTV = findViewById(R.id.userName);




        micBtn = findViewById(R.id.micBtn);
        deafenBtn = findViewById(R.id.deafenBtn);

        codeTV.setText(room.getCode());
        userNameTV.setText(room.getUser().getUserId());

        if (room.getRoomType() == RoomType.PENDING){
            setMicImage();
            setDeafenImage();
        }
        AspectRatio.set(this);

    }

    private void captureWebView(){
        Bitmap bitmap = Bitmap.createBitmap(webView.getWidth(),webView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        webView.draw(canvas);
        File externalDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File filePath = new File(externalDir, System.currentTimeMillis()+".jpg");
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(filePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
            outputStream.close();
            Toast.makeText(this, "Image Saved!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void attachOnlinePlayerControlLayout() {
        onlinePlayerControlLayout = (ConstraintLayout) LayoutInflater.from(this)
                .inflate(R.layout.custom_controls_online, null);
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
        );
        onlinePlayerControlLayout.setLayoutParams(layoutParams);
        onlinePlayerLayout.addView(onlinePlayerControlLayout);
    }

    @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled"})
    private void setupWebView() {
        webView = findViewById(R.id.onlinePlayerWebView);
        webView.getSettings().setJavaScriptEnabled(true);
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
                //callJavaScript("javascript:init(\"" + room.getUser().getUserId() + "\")");
            }

        });
        jsInterface = new JSinterface(this);
        webView.addJavascriptInterface(jsInterface, "Android");

        WebSettings webSettings = webView.getSettings();
        webSettings.setDomStorageEnabled(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false);

        String path = "file:android_asset/youtube_iframe_api.html";
        WebView.enableSlowWholeDocumentDraw();
        //setContentView(webView);
        webView.loadUrl(path);


    }
    public void callJavaScript(String func) {
        webView.evaluateJavascript(func, null);
    }


    public void createYouTubeUrl(View view) {
        if (youtubeUrlET.getText() == null || youtubeUrlET.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please Enter Url", Toast.LENGTH_SHORT).show();
            return;
        }
        String url = youtubeUrlET.getText().toString();
        OnlineVideo onlineVideo = new OnlineVideo(userModel.getUserId(),userModel.getName(),url);
        FirebaseUtils.updateOnlineVideo(room.getCode(), onlineVideo, new FirebaseListener() {
            @Override
            public void onComplete(boolean successful, ListenerData data) {
                Toast.makeText(PlayerActivity.this, "YouTube url created!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @SuppressLint("SetTextI18n")
    private void setUpOnlineVideo() {
        EventListenerData listenerData = FirebaseUtils.getOnlineVideo(room.getCode(), new FirebaseListener() {

            @Override
            public void onComplete(boolean successful, ListenerData data) {
                if (successful){
                    currentOnlineVideoTxt.setVisibility(View.VISIBLE);
                    currentOnlineVideoTxt.setText("Current Url: "+data.getOnlineVideo().getYoutubeUrl());
                }else{
                    currentOnlineVideoTxt.setVisibility(View.GONE);
                }
            }
        });
        HandleEventListener.add(this,listenerData);
    }

    private void setUpListener() {

        onlinePlayerListener = new OnlinePlayerListener() {
            @Override
            public void onUpdateCurrentDuration(int seconds) {
                String currentDuration = Base.formatSeconds(seconds);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onlinePlayerCurrentDuration.setText(currentDuration);
//                        if (totalDuration < seconds && onlinePlayerTotalDuration.getText().equals(getString(R.string._00_00))){
//                            totalDuration = seconds;
//                            onlinePlayerSeekBar.setDuration(seconds);
//                        }
                        if (!isSeekBarTouched) {
                            onlinePlayerSeekBar.setPosition(seconds);
                        }
                    }
                });
            }

            @Override
            public void onUpdateTotalDuration(int seconds) {
//                totalDuration = seconds;
                String totalDuration = Base.formatSeconds(seconds);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onlinePlayerTotalDuration.setText(totalDuration);
                        onlinePlayerSeekBar.setDuration(seconds);
                    }
                });
            }
        };
        listener = new PlayerActivityListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onReceiveMessage(MessageModel messageModel) {
            }

            @Override
            public void onToogleMic() {
                toogleMic(false);
            }

            @Override
            public void onDisconnect() {
                finish();
            }

            @Override
            public void onToogleDeafen() {
                toogleDeafen(false);
            }

            @Override
            public void onSeekInfo(String id, long positionMs) {
                isListenerCommand = true;
                if (player == null){
                    return;
                }

                runOnUiThread(() -> {

                    long targetPosition = Math.min(positionMs + 300, player.getDuration());
                    player.seekTo(targetPosition);

                });
            }

            @Override
            public void onPlayPauseInfo(String id, boolean isPlaying) {
                isListenerCommand = true;
                if (player == null){
                    return;
                }
                runOnUiThread(() -> {
                    playAndPause(!isPlaying);
                });
            }

            @Override
            public void onPlaybackStateRequest(String id) {
                if (player == null){
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CallService.listener.onSendPlaybackState(id,player.isPlaying(),player.getCurrentPosition());
                    }
                });
            }

            @Override
            public void onPlaybackStateRecevied(String id, boolean isPlaying, long positionMs) {
                if (!isFirstSync){
                    return;
                }
                if (player == null){
                    return;
                }
                runOnUiThread(() -> {
                    if (!isFirstSync){
                        return;
                    }
                    isFirstSync = false;
                    long targetPosition = Math.min(positionMs + 800, player.getDuration());
                    isListenerCommand = true;
                    player.seekTo(targetPosition);
                    isListenerCommand = true;
                    playAndPause(!isPlaying);
                    isListenerCommand = false;
                    if (isPartyStopped){
                        isPartyStopped = false;
                        CallService.listener.onSendJoinedPartyAgain();
                    }

                });
            }

            @Override
            public void onScale(float scaleFactor) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView textView = findViewById(R.id.live_feed);
                        textView.setText(scaleFactor+"");
                    }
                });
            }
        };
    }








//    @Override
//    public void onConfigurationChanged(@NonNull Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        Menu.changeMenu(this, newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE);
//    }


    @OptIn(markerClass = UnstableApi.class)
    public void playVideo(Uri videoUri) {
        playerUtil = new PlayerUtil(this);
        isFirstSync = true;
        player = new ExoPlayer.Builder(this)
//                .setTrackSelector(trackSelector)
                .build();
        playerView.setPlayer(player);
        playerView.setControllerAutoShow(false);
        MediaItem mediaItem = new MediaItem.Builder()
                .setUri(videoUri)
                .build();
        //        player.addListener(new PlayerEventListener());
        player.setMediaItem(mediaItem);
//        player.setAudioAttributes(mPlaybackAttributes, true);
        player.prepare();
        player.setRepeatMode(REPEAT_MODE_ONE);
        player.play();
        playerView.onResume();

        resetPlayerViews();
        playerView.setOnTouchListener(new TouchGesture(this, playerView, player));

        playerUtil.addSeekListener(player, new PlayerListener() {
            @Override
            public void onSeek(long positionMs) {
                if (isListenerCommand){
                    isListenerCommand = false;
                    return;
                }
                runOnUiThread(() -> CallService.listener.onSendSeekInfo(positionMs));

            }

            @Override
            public void onIsPlaying(boolean isPlaying) {
                if (isListenerCommand){
                    isListenerCommand = false;
                    return;
                }
                runOnUiThread(() -> CallService.listener.onSendPlayPauseInfo(isPlaying));

            }

            @Override
            public void onPlayerReady() {
                runOnUiThread(() -> CallService.listener.onSendPlaybackStateRequest());

            }
        });


    }

    private void resetPlayerViews() {
        playPause.setImageResource(R.drawable.exo_pause);
        playbackSpeed = 2;
        muteUnmute.setImageResource(R.drawable.volume_on);
        imgCC.setImageResource(R.drawable.cc_on);
    }


    public void sendMessage(View view) {
        PeerManagement.listener.onSendMessage();
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    public void toggleLayout(View view) {
        PeerManagement.listener.onToggleLayout(view);
    }

    public void mic(View view) {
        Info.isMute = !Info.isMute;
        toogleMic(true);
    }
    public void deafen(View view) {
        Info.isDeafen = !Info.isDeafen;
        toogleDeafen(true);
    }
    private void toogleMic(boolean isTap) {

        if (isTap){
            CallService.listener.onToogleMic();
        }
        setMicImage();
    }
    private void toogleDeafen(boolean isTap) {

        if (isTap){
            CallService.listener.onToogleDeafen();
        }
        setDeafenImage();
    }

    private void setDeafenImage() {
        if(Info.isDeafen){
            deafenBtn.setImageResource(R.drawable.deafen_on);
            micBtn.setVisibility(View.GONE);
        }
        else{
            deafenBtn.setImageResource(R.drawable.deafen_off);
            micBtn.setVisibility(View.VISIBLE);
        }
    }

    private void setMicImage() {
        if(Info.isMute){
            micBtn.setImageResource(R.drawable.mic_off);
        }
        else{
            micBtn.setImageResource(R.drawable.mic_on);
        }
    }

    public void endCall(View view) {
        finish();
        if (CallService.listener != null){
            CallService.listener.onDisconnect();
        }
    }


    public void playAndPause(View view) {
        playAndPause(player.isPlaying());
    }

    public void playAndPause(boolean isPlaying){
        if (isPlaying){
            player.pause();
            playerUtil.unsetState();
            playPause.setImageResource(R.drawable.exo_play);
        }else {
            player.play();
            playPause.setImageResource(R.drawable.exo_pause);
        }
    }

    public void test(View view) {
        //webTouchGesture.resize();
//        float x = AspectRatio.value(1.22f);
//        float x = Base.getZoomFactor(1.22f);
//        webView.setScaleX(x);
//        webView.setScaleY(x);
//
//        Toast.makeText(this, ""+x, Toast.LENGTH_SHORT).show();
    }


    public void fullScreen(View view) {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        ImageView fullscreen = (ImageView) findViewById(R.id.exo_screen);
        View decorView = getWindow().getDecorView();

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            fullscreen.setImageResource(R.drawable.fullscreen_exit);
            webFullScreen.setImageResource(R.drawable.fullscreen_exit);
            hideLayout(true);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                getWindow().getAttributes().layoutInDisplayCutoutMode =
                        WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            }
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            chatUtil.activate(false);
        }
        else {
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            fullscreen.setImageResource(R.drawable.fullscreen);
            webFullScreen.setImageResource(R.drawable.fullscreen);
            hideLayout(false);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                getWindow().getAttributes().layoutInDisplayCutoutMode =
                        WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT;
            }
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            chatUtil.disable(false);
        }
    }

    private void hideLayout(boolean hide) {
        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) playerLayout.getLayoutParams();
        if (hide){
            layout1.setVisibility(View.GONE);
            partyLayout.setVisibility(View.GONE);
            lp.dimensionRatio = "";

        }else{
            layout1.setVisibility(View.VISIBLE);
            partyLayout.setVisibility(View.VISIBLE);
            lp.dimensionRatio = "1.77";
        }
        playerLayout.setLayoutParams(lp);
    }

    public void chat(View view) {

        if (partyLayout.getVisibility() != View.VISIBLE){
            chatUtil.activate(true);
        }else{
            chatUtil.disable(true);
        }

    }

    public void closeLive(View view) {
        if (chatUtil != null){
            chatUtil.disable(true);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void toogleAddLayout(boolean online) {

        if(online){
            offlineAddLayout.setVisibility(View.GONE);
            onlineAddLayout.setVisibility(View.VISIBLE);
            offlineAdd.setBackground(getDrawable(R.drawable.bg_not_selected));
            onlineAdd.setBackground(getDrawable(R.drawable.bg_selected));
            offlineAdd.setTextColor(getColor(R.color.white_200));
            onlineAdd.setTextColor(getColor(R.color.theme_color));
        }else{
            offlineAddLayout.setVisibility(View.VISIBLE);
            onlineAddLayout.setVisibility(View.GONE);
            onlineAdd.setBackground(getDrawable(R.drawable.bg_not_selected));
            offlineAdd.setBackground(getDrawable(R.drawable.bg_selected));
            offlineAdd.setTextColor(getColor(R.color.theme_color));
            onlineAdd.setTextColor(getColor(R.color.white_200));
        }

    }


    public void onlineAddLayout(View view) {
        toogleAddLayout(true);
    }

    public void offlineAddLayout(View view) {
        toogleAddLayout(false);
    }

    ActivityResultLauncher<Intent> pickVideoLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK){
                Intent data = result.getData();
                if (data != null) {
                    videoUri = data.getData();
                    if (videoUri != null) {
                        currentMediaTV.setText(videoUri.toString());
                        currentMediaTV.setVisibility(View.VISIBLE);
                        playOffileVideo.setVisibility(View.VISIBLE);
                    }
                }
            }

        }
    });

    public void selectVideo(View view) {
        PickerUtil.pickVideo(pickVideoLauncher);
    }


    public void startSyncPlay(View view) {
        if (videoUri == null){
            return;
        }
        addMediaLayout.setVisibility(View.GONE);
        allPlayerLayout.setVisibility(View.VISIBLE);
        exoplayerLayout.setVisibility(View.VISIBLE);
        playVideo(videoUri);
    }


    public void refreshLayout(View view) {
        refreshLayout();
    }

    private void refreshLayout() {
        allPlayerLayout.setVisibility(View.GONE);
        exoplayerLayout.setVisibility(View.GONE);
        onlinePlayerLayout.setVisibility(View.GONE);
        addMediaLayout.setVisibility(View.VISIBLE);
        releasePlayer();
        releaseWebView();
    }

    private void releaseWebView() {
        if (webView != null) {
            webView.loadUrl("");
            webView = null;
        }

        isYouTubePlaying = false;
        webPlayPause.setImageResource(R.drawable.exo_pause);
        isYouTubeMute = false;
        webMuteUnmute.setImageResource(R.drawable.volume_on);
        onlinePlayerCurrentDuration.setText(getString(R.string._00_00));
        onlinePlayerTotalDuration.setText(getString(R.string._00_00));

    }

    private void releasePlayer() {
        if (player != null){
            playerUtil.removeListener();
            player.release();
            player = null;
            playerView.setPlayer(null);
            if (CallService.listener != null){
                isPartyStopped = true;
                CallService.listener.onActivityStopInfo();
            }

        }
    }


    public void changeVideo(View view){
        if (!isShowingTrackSelectionDialog && TrackSelectionDialog.willHaveContent(player)) {
            isShowingTrackSelectionDialog = true;
            TrackSelectionDialog trackSelectionDialog =
                    TrackSelectionDialog.createForPlayer(
                            player,
                            /* onDismissListener= */ dismissedDialog -> isShowingTrackSelectionDialog = false);
            trackSelectionDialog.show(getSupportFragmentManager(), /* tag= */ null);
        }
    }


    public void lock(View view){
        findViewById(R.id.ctrlLayout).setVisibility(View.GONE);
        findViewById(R.id.big_lock).setVisibility(View.VISIBLE);
    }
    public void unlock(View view) {
        view.setVisibility(View.GONE);
        findViewById(R.id.ctrlLayout).setVisibility(View.VISIBLE);
    }

    public void cc(View view){
        if (playerView.getSubtitleView() == null){
            return;
        }
        if(playerView.getSubtitleView().getVisibility() == View.VISIBLE){
            imgCC.setImageResource(R.drawable.cc_off);
            playerView.getSubtitleView().setVisibility(View.GONE);

        }
        else{
            imgCC.setImageResource(R.drawable.cc_on);
            playerView.getSubtitleView().setVisibility(View.VISIBLE);
        }
    }
    public void muteUnmute(View view){
        if(player == null){
            return;
        }
        if(player.getVolume() == 0f){
            muteUnmute.setImageResource(R.drawable.volume_on);
            player.setVolume(1f);
        }
        else{
            muteUnmute.setImageResource(R.drawable.volume_off);
            player.setVolume(0f);
        }
    }

    public void enterPIP(View view){
        Display d = getWindowManager().getDefaultDisplay();
        Point p = new Point();
        d.getSize(p);
        Rational ratio = new Rational(16,9);
        //ratio = new Rational(dimension(vidUri)[0],dimension(vidUri)[1]);
        PictureInPictureParams.Builder pipBuilder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            playerView.hideController();
            pipBuilder = new PictureInPictureParams.Builder();
            pipBuilder.setAspectRatio(ratio).build();
            enterPictureInPictureMode(pipBuilder.build());
            pip = true;
        }

    }

    public void changeSpeed(View view){
        if (player == null){
            return;
        }
        AlertDialog.Builder playbackDialog = new AlertDialog.Builder(this);
        playbackDialog.setTitle("Change Playback Speed").setPositiveButton("OK",null);
        String[] items = {"0.25x","0.5x","1x (normal)","1.25x","1.5x","2x"};
        playbackDialog.setSingleChoiceItems(items, playbackSpeed, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                playbackSpeed = i;
                switch (i){
                    case 0:
                        player.setPlaybackParameters(new PlaybackParameters(.25f));
                        break;
                    case 1:
                        player.setPlaybackParameters(new PlaybackParameters(.5f));
                        break;
                    case 2:
                        player.setPlaybackParameters(new PlaybackParameters(1f));
                        break;
                    case 3:
                        player.setPlaybackParameters(new PlaybackParameters(1.25f));
                        break;
                    case 4:
                        player.setPlaybackParameters(new PlaybackParameters(1.5f));
                        break;
                    case 5:
                        player.setPlaybackParameters(new PlaybackParameters(2f));
                        break;
                    default:
                        break;
                }
            }
        });
        playbackDialog.show();

    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //listener = null;
        if (playerView != null) {
            playerView.onPause();
        }
        releasePlayer();
        HandleEventListener.removeAll(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (pip){
            pip = false;
            //onlinePlayerLayout.setVisibility(View.VISIBLE);
        }
        if (webpip){
            webpip = false;
            onlinePlayerLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (pip){
            endCall(null);
            return;
        }
        if (playerView != null) {
            playerView.onPause();
        }
        releasePlayer();




    }

    @Override
    protected void onStart() {
        super.onStart();

        refreshLayout();
    }

    @SuppressLint("MissingInflatedId")
    private void showExitPartyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.do_you_want_to_exit_the_party));
        builder.setPositiveButton("EXIT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                endCall(null);
            }
        });
        builder.show();
    }
    @Override
    public void onBackPressed() {
        showExitPartyDialog();
//        super.onBackPressed();
    }


    @SuppressLint("ClickableViewAccessibility")
    public void joinYouTubeVideo(View view) {
        if (webView == null){
            setupWebView();
        }
        addMediaLayout.setVisibility(View.GONE);
        allPlayerLayout.setVisibility(View.VISIBLE);
        onlinePlayerLayout.setVisibility(View.VISIBLE);
        webTouchGesture = new WebTouchGesture(this,webView,onlinePlayerListener,onlinePlayerControlLayout);
        webView.setOnTouchListener(webTouchGesture);
    }

    public void webPlayPause(View view) {

        isYouTubePlaying = !isYouTubePlaying;
        callJavaScript("javascript:playPauseVideo(" + !isYouTubePlaying + ")");
        if (isYouTubePlaying){
            webPlayPause.setImageResource(R.drawable.exo_play);
        }else{
            webPlayPause.setImageResource(R.drawable.exo_pause);
        }

    }


    public void webMuteUnmute(View view) {
        isYouTubeMute = !isYouTubeMute;
        callJavaScript("javascript:setPlayerMute(" + isYouTubeMute + ")");
        if (isYouTubeMute){
            webMuteUnmute.setImageResource(R.drawable.volume_off);
        }else {
            webMuteUnmute.setImageResource(R.drawable.volume_on);
        }
    }

    public void webViewClick(View view) {
    }

    public void webLock(View view) {
        onlinePlayerLayout.findViewById(R.id.ctrlLayout).setVisibility(View.GONE);
        onlinePlayerLayout.findViewById(R.id.big_lock).setVisibility(View.VISIBLE);
    }

    public void webcc(View view) {
    }

    public void enterWebPIP(View view) {
        Display d = getWindowManager().getDefaultDisplay();
        Point p = new Point();
        d.getSize(p);
        Rational ratio = new Rational(16,9);
        //ratio = new Rational(dimension(vidUri)[0],dimension(vidUri)[1]);
        PictureInPictureParams.Builder pipBuilder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            onlinePlayerLayout.setVisibility(View.GONE);
            pipBuilder = new PictureInPictureParams.Builder();
            pipBuilder.setAspectRatio(ratio).build();
            enterPictureInPictureMode(pipBuilder.build());
            webpip = true;
        }
    }

    public void webUnlock(View view) {
        view.setVisibility(View.GONE);
        onlinePlayerLayout.findViewById(R.id.ctrlLayout).setVisibility(View.VISIBLE);
    }
}