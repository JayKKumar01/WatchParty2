package com.github.jaykkumar01.watchparty;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PictureInPictureParams;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.view.View;
import android.view.WindowManager;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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
import androidx.media3.common.util.UnstableApi;
import com.github.jaykkumar01.watchparty.enums.RoomType;
import com.github.jaykkumar01.watchparty.helpers.HandleEventListener;
import com.github.jaykkumar01.watchparty.helpers.PeerManagement;
import com.github.jaykkumar01.watchparty.helpers.PlayerManagement;
import com.github.jaykkumar01.watchparty.helpers.RecycleViewManagement;
import com.github.jaykkumar01.watchparty.interfaces.FirebaseListener;
import com.github.jaykkumar01.watchparty.libs.OnlinePlayerView;
import com.github.jaykkumar01.watchparty.models.EventListenerData;
import com.github.jaykkumar01.watchparty.models.ListenerData;
import com.github.jaykkumar01.watchparty.models.OnlineVideo;
import com.github.jaykkumar01.watchparty.models.Room;
import com.github.jaykkumar01.watchparty.models.UserModel;
import com.github.jaykkumar01.watchparty.services.CallService;
import com.github.jaykkumar01.watchparty.update.Info;
import com.github.jaykkumar01.watchparty.utils.AspectRatio;
import com.github.jaykkumar01.watchparty.utils.AutoRotate;
import com.github.jaykkumar01.watchparty.utils.ChatUtil;
import com.github.jaykkumar01.watchparty.utils.FirebaseUtils;
import com.github.jaykkumar01.watchparty.utils.PickerUtil;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@OptIn(markerClass = UnstableApi.class)
public class PlayerActivity extends AppCompatActivity {
    Room room;
    TextView codeTV,userNameTV;

    public static Listener listener;

    ConstraintLayout layout1,partyLayout,playerLayout;
    TextView offlineAdd,onlineAdd;
    ConstraintLayout offlineAddLayout, onlineAddLayout;
    Uri videoUri;
    TextView currentMediaTV,playOffileVideo;
    ConstraintLayout addMediaLayout;

    private int count;

    private boolean pip;


    private UserModel userModel;
    private ChatUtil chatUtil;
    private ImageView imgChat;
    private ImageView liveClose;
    private TextView currentOnlineVideoTxt;
    private TextInputEditText youtubeUrlET;
    private AppCompatButton joinYouTube,createYouTube;
    private ConstraintLayout exoplayerLayout,onlinePlayerLayout,allPlayerLayout;
    private ImageView micBtn,deafenBtn;

    private PlayerManagement playerManagement;
    private OnlinePlayerView onlinePlayerView;

    public interface Listener{
        void onToggleMic();
        void onDisconnect();
        void onToggleDeafen();
    }


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

        RecycleViewManagement.start(this,room);
        PeerManagement.start(this,room);
        playerManagement = new PlayerManagement(this,getSupportFragmentManager());
        onlinePlayerLayout = findViewById(R.id.online_player_view);
        onlinePlayerView = new OnlinePlayerView(this,onlinePlayerLayout);



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

        allPlayerLayout = findViewById(R.id.all_player_view);

        //attachOnlinePlayerControlLayout();






        //setupWebView();

        currentMediaTV = findViewById(R.id.currentMediaTxt);
        playOffileVideo = findViewById(R.id.playOffileVideo);

        currentOnlineVideoTxt = findViewById(R.id.currentOnlineVideoTxt);
        youtubeUrlET = findViewById(R.id.youtubeUrlET);
        joinYouTube = findViewById(R.id.btnJoinYouTube);
        createYouTube = findViewById(R.id.btnCreateYouTube);

        setUpOnlineVideo();


        addMediaLayout = findViewById(R.id.addMediaLayout);

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

        listener = new Listener() {

            @Override
            public void onToggleMic() {
                toogleMic(false);
            }

            @Override
            public void onDisconnect() {
                finish();
            }

            @Override
            public void onToggleDeafen() {
                toogleDeafen(false);
            }
        };
    }








//    @Override
//    public void onConfigurationChanged(@NonNull Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        Menu.changeMenu(this, newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE);
//    }







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
        playerManagement.playAndPause();
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
        playerManagement.fullScreen();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        OnlinePlayerView.listener.onConfigChange(newConfig);
        ImageView fullscreen = (ImageView) findViewById(R.id.exo_screen);
        View decorView = getWindow().getDecorView();

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            fullscreen.setImageResource(R.drawable.fullscreen_exit);
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
        playerManagement.playVideo(videoUri);
    }


    public void refreshLayout(View view) {
        refreshLayout();
    }

    private void refreshLayout() {
        allPlayerLayout.setVisibility(View.GONE);
        exoplayerLayout.setVisibility(View.GONE);
        onlinePlayerLayout.setVisibility(View.GONE);
        addMediaLayout.setVisibility(View.VISIBLE);
        playerManagement.releasePlayer();
        onlinePlayerView.stop();
        //releaseWebView();
    }




    public void changeVideo(View view){
        playerManagement.changeVideo();
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
        playerManagement.CC();
    }
    public void muteUnmute(View view){
        playerManagement.muteUnmute();
    }

    public void enterPIP(View view){
        Display d = getWindowManager().getDefaultDisplay();
        Point p = new Point();
        d.getSize(p);
        Rational ratio = new Rational(16,9);
        //ratio = new Rational(dimension(vidUri)[0],dimension(vidUri)[1]);
        PictureInPictureParams.Builder pipBuilder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            playerManagement.hideController();

            pipBuilder = new PictureInPictureParams.Builder();
            pipBuilder.setAspectRatio(ratio).build();
            enterPictureInPictureMode(pipBuilder.build());
            pip = true;
        }

    }

    public void changeSpeed(View view){
        playerManagement.changeSpeed();

    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        playerManagement.releasePlayer();
        HandleEventListener.removeAll(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (pip){
            pip = false;
            //onlinePlayerLayout.setVisibility(View.VISIBLE);
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
        playerManagement.releasePlayer();




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
        onlinePlayerView.start();
        addMediaLayout.setVisibility(View.GONE);
        allPlayerLayout.setVisibility(View.VISIBLE);
        onlinePlayerLayout.setVisibility(View.VISIBLE);

        //webView.setOnTouchListener(webTouchGesture);
    }

    public void webPlayPause(View view) {



    }


    public void webMuteUnmute(View view) {

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
//            webpip = true;
        }
    }

    public void webUnlock(View view) {
        view.setVisibility(View.GONE);
        onlinePlayerLayout.findViewById(R.id.ctrlLayout).setVisibility(View.VISIBLE);
    }
}