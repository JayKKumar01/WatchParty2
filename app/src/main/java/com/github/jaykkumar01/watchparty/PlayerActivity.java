package com.github.jaykkumar01.watchparty;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PictureInPictureParams;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Rational;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
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
import com.github.jaykkumar01.watchparty.libs.models.YouTubeData;
import com.github.jaykkumar01.watchparty.models.EventListenerData;
import com.github.jaykkumar01.watchparty.models.ListenerData;
import com.github.jaykkumar01.watchparty.models.OnlineVideo;
import com.github.jaykkumar01.watchparty.models.Room;
import com.github.jaykkumar01.watchparty.models.SampleData;
import com.github.jaykkumar01.watchparty.models.UserModel;
import com.github.jaykkumar01.watchparty.services.CallService;
import com.github.jaykkumar01.watchparty.update.Info;
import com.github.jaykkumar01.watchparty.utils.AspectRatio;
import com.github.jaykkumar01.watchparty.utils.AutoRotate;
import com.github.jaykkumar01.watchparty.utils.ChatUtil;
import com.github.jaykkumar01.watchparty.utils.FirebaseUtils;
import com.github.jaykkumar01.watchparty.utils.ObjectUtil;
import com.github.jaykkumar01.watchparty.utils.PickerUtil;
import com.github.jaykkumar01.watchparty.libs.utils.YouTubeAPI;
import com.google.android.material.textfield.TextInputEditText;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    private YouTubeData youTubeData;

    public interface Listener{

        void onToggleMic();
        void onDisconnect();
        void onToggleDeafen();

        void onShow(boolean b);

        void onPip();

        void onChatClick();

        void onMessage(String message);

        void onResult(String s);
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
//        Toast.makeText(this, ""+ YouTubeUtil.extractVideoId(url), Toast.LENGTH_SHORT).show();
        youTubeData = new YouTubeData(url,null);
        joinYouTubeVideo(null);

        OnlineVideo onlineVideo = new OnlineVideo(userModel.getUserId(),userModel.getName(),url);

        YouTubeAPI youTubeAPI = new YouTubeAPI(this,url);
        youTubeAPI.setListener((success, youTubeData) -> {
            if (success){
                onlineVideo.setYouTubeData(youTubeData);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        FirebaseUtils.updateOnlineVideo(room.getCode(), onlineVideo, (successful, data) ->
                                Toast.makeText(PlayerActivity.this, "Current YouTube video updated!", Toast.LENGTH_SHORT).show());
                    }
                });

            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PlayerActivity.this, "couldn't get details", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(youTubeAPI);


    }

    @SuppressLint("SetTextI18n")
    private void setUpOnlineVideo() {
        EventListenerData listenerData = FirebaseUtils.getOnlineVideo(room.getCode(), new FirebaseListener() {

            @Override
            public void onComplete(boolean successful, ListenerData data) {
                if (successful){
                    currentOnlineVideoTxt.setVisibility(View.VISIBLE);
                    youTubeData = data.getOnlineVideo().getYouTubeData();
                    currentOnlineVideoTxt.setText("Current Video: "+youTubeData.getTitle());
                }else{
                    currentOnlineVideoTxt.setVisibility(View.GONE);
                    youTubeData = null;
                }
            }
        });
        HandleEventListener.add(this,listenerData);
    }


    private void setUpListener() {

        listener = new Listener() {

            @Override
            public void onResult(String s) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PlayerActivity.this, s, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onMessage(String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String msg = StringEscapeUtils.unescapeJava(message);
                        Log.d("message",msg);
                        Toast.makeText(PlayerActivity.this, ""+msg, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onChatClick() {
                runOnUiThread(() -> {
                    if (partyLayout.getVisibility() != View.VISIBLE){
                        chatUtil.activate(true);
                    }else{
                        chatUtil.disable(true);
                    }
                });
            }

            @Override
            public void onPip() {
                pip = true;
            }

            @Override
            public void onShow(boolean b) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PlayerActivity.this, ""+b, Toast.LENGTH_SHORT).show();
                    }
                });

            }

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
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        OnlinePlayerView.listener.onConfigChange(newConfig);
        PlayerManagement.listener.onConfigChange(newConfig);
        View decorView = getWindow().getDecorView();
        hideSoftKeyboard();


        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
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

    private void hideSoftKeyboard() {
        try {
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
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

        if (youTubeData == null){
            Toast.makeText(this, "No current video", Toast.LENGTH_SHORT).show();
            return;
        }



        onlinePlayerView.start(youTubeData.getLink());
        addMediaLayout.setVisibility(View.GONE);
        allPlayerLayout.setVisibility(View.VISIBLE);
        onlinePlayerLayout.setVisibility(View.VISIBLE);
    }
}