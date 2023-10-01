package com.github.jaykkumar01.watchparty;

import static com.google.android.exoplayer2.Player.REPEAT_MODE_ONE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.jaykkumar01.watchparty.adapters.ChatAdapter;
import com.github.jaykkumar01.watchparty.adapters.UserAdapter;
import com.github.jaykkumar01.watchparty.enums.RoomType;
import com.github.jaykkumar01.watchparty.interfaces.PlayerActivityListener;
import com.github.jaykkumar01.watchparty.interfaces.PlayerListener;
import com.github.jaykkumar01.watchparty.models.EventListenerData;
import com.github.jaykkumar01.watchparty.models.MessageModel;
import com.github.jaykkumar01.watchparty.models.Room;
import com.github.jaykkumar01.watchparty.models.UserModel;
import com.github.jaykkumar01.watchparty.services.CallService;
import com.github.jaykkumar01.watchparty.update.Info;
import com.github.jaykkumar01.watchparty.utils.AutoRotate;
import com.github.jaykkumar01.watchparty.utils.FirebaseUtils;
import com.github.jaykkumar01.watchparty.utils.PickerUtil;
import com.github.jaykkumar01.watchparty.utils.PlayerUtil;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.StyledPlayerView;

import java.util.ArrayList;
import java.util.List;

public class PlayerActivity extends AppCompatActivity {
    Room room;
    TextView codeTV,userNameTV;
    EditText messageET;
    RecyclerView userListRV,chatListRV;
    List<EventListenerData> eventListenerList = new ArrayList<>();
    private UserAdapter userAdapter;
    private List<UserModel> userList = new ArrayList<>();
    private ChatAdapter chatAdapter;

    public static PlayerActivityListener listener;
    ConstraintLayout chatLayout,peerLayout;
    ImageView micBtn,deafenBtn;
    ImageView circle;
    int peerCount;

    StyledPlayerView playerView;
    Player player;

    ConstraintLayout layout1,partyLayout,playerLayout;
    TextView offlineAdd,onlineAdd;
    ConstraintLayout offlineAddLayout, onlineAddLayout;
    Uri videoUri;
    TextView currentMediaTV,playOffileVideo;
    ConstraintLayout addMediaLayout;
    private boolean isListenerCommand;
    ImageView playPause;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            getWindow().getAttributes().layoutInDisplayCutoutMode =
//                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
//        }
        setContentView(R.layout.activity_player);
        room = (Room) getIntent().getSerializableExtra(getString(R.string.room));
        if (room == null){
            return;
        }

        AutoRotate.set(this);
        setUpListener();

        layout1 = findViewById(R.id.LYOUT1);
        partyLayout = findViewById(R.id.partyLayout);
        playerLayout = findViewById(R.id.playerLayout);

        offlineAdd = findViewById(R.id.offlineBtn);
        onlineAdd = findViewById(R.id.onlineBtn);
        offlineAddLayout = findViewById(R.id.offlineMediaLayout);
        onlineAddLayout = findViewById(R.id.onlineMediaLayout);

        currentMediaTV = findViewById(R.id.currentMediaTxt);
        playOffileVideo = findViewById(R.id.playOffileVideo);

        addMediaLayout = findViewById(R.id.addMediaLayout);
        playerView = findViewById(R.id.player_view);
        playPause = findViewById(R.id.play_pause);

        codeTV = findViewById(R.id.roomCode);
        userNameTV = findViewById(R.id.userName);

        chatLayout = findViewById(R.id.chatLayout);
        peerLayout = findViewById(R.id.peerLayout);
        circle = findViewById(R.id.circle);

        micBtn = findViewById(R.id.micBtn);
        deafenBtn = findViewById(R.id.deafenBtn);

        userListRV = findViewById(R.id.recyclerViewUsers);
        setupUsersRecycleView(userListRV);
        chatListRV = findViewById(R.id.recyclerViewChats);
        setupChatsRecycleView(chatListRV);

        codeTV.setText(room.getCode());
        userNameTV.setText(room.getUser().getUserId());

        messageET = findViewById(R.id.messageTXT);

        if (room.getRoomType() == RoomType.PENDING){
            setMicImage();
            setDeafenImage();
        }

    }

    private void setUpListener() {
        listener = new PlayerActivityListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onReceiveMessage(MessageModel messageModel) {
                runOnUiThread(() -> {
                    if (chatLayout.getVisibility() != View.VISIBLE){
                        circle.setVisibility(View.VISIBLE);
                    }
                    chatAdapter.addMessage(messageModel);
                    chatAdapter.notifyDataSetChanged();
                    if (chatListRV.getScrollState() == RecyclerView.SCROLL_STATE_IDLE){
                        scrollToTop(chatListRV);
                    }
                });
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

                    long targetPosition = Math.min(positionMs + 500, player.getDuration());
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
                if (player == null){
                    return;
                }
                runOnUiThread(() -> {
                    long targetPosition = Math.min(positionMs + 800, player.getDuration());
                    isListenerCommand = true;
                    player.seekTo(targetPosition);
                    isListenerCommand = true;
                    playAndPause(!isPlaying);
                });
            }
        };
    }

    private void setupChatsRecycleView(RecyclerView recyclerView) {

        recyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);
        chatAdapter = new ChatAdapter(this,room.getUser().getUserId());
        recyclerView.setAdapter(chatAdapter);


    }
    private void scrollToTop(RecyclerView recyclerView) {
        recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());
    }

    private void setupUsersRecycleView(RecyclerView userListRV) {
        userListRV.setLayoutManager(new LinearLayoutManager(this));
        userAdapter = new UserAdapter(userList);
        userListRV.setAdapter(userAdapter);


        @SuppressLint("NotifyDataSetChanged")
        EventListenerData listenerData = FirebaseUtils.getUserList(room.getCode(), (successful, data) -> {
            if (successful){
                userList = data.getUserList();

                if (!room.isPeerConnected()){
                    room.setPeerConnected(true);
                    room.setUserList(userList);
                    Intent serviceIntent = new Intent(this, CallService.class);
                    serviceIntent.putExtra(getString(R.string.room),room);
                    startService(serviceIntent);
//                    Toast.makeText(this, "Sevice Started", Toast.LENGTH_SHORT).show();
                }

                if (peerLayout.getVisibility() != View.VISIBLE){
                    circle.setVisibility(View.VISIBLE);
                }
                peerCount = userList.size();
                userAdapter.setList(userList);
                userAdapter.notifyDataSetChanged();

                if (userList.isEmpty()){
                    Toast.makeText(PlayerActivity.this, data.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(PlayerActivity.this, data.getErrorMessage()+"", Toast.LENGTH_SHORT).show();
            }
        });
        eventListenerList.add(listenerData);


    }



//    @Override
//    public void onConfigurationChanged(@NonNull Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        Menu.changeMenu(this, newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE);
//    }


    public void playVideo(Uri videoUri) {

        player = new ExoPlayer.Builder(this).build();
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


        PlayerUtil.addSeekListener(player, new PlayerListener() {
            @Override
            public void onSeek(long positionMs) {
                if (isListenerCommand){
                    isListenerCommand = false;
                    return;
                }
                CallService.listener.onSendSeekInfo(positionMs);
            }

            @Override
            public void onIsPlaying(boolean isPlaying) {
                if (isListenerCommand){
                    isListenerCommand = false;
                    return;
                }
                CallService.listener.onSendPlayPauseInfo(isPlaying);
            }

            @Override
            public void onPlayerReady() {
                CallService.listener.onSendPlaybackStateRequest();
            }


        });


    }



    public void sendMessage(View view) {
        if (messageET.getText() == null || messageET.getText().toString().isEmpty()){
            return;
        }
        if (peerCount < 2){
            Toast.makeText(this, "Invite Friends to chat!", Toast.LENGTH_SHORT).show();
            return;
        }
        String message = messageET.getText().toString();
        MessageModel messageModel = new MessageModel(room.getUser().getUserId(),message);
        messageModel.setName(room.getUser().getName());
        messageModel.setTimeMillis(System.currentTimeMillis());
        CallService.listener.sendMessage(messageModel);
        messageET.setText("");

        listener.onReceiveMessage(messageModel);

//        Locale locale = Resources.getSystem().getConfiguration().getLocales().get(0);
//        SimpleDateFormat dateFormat = new SimpleDateFormat("dd LLL yyyy",locale);
//        String msgDate = dateFormat.format(messageModel.getTimeMillis());
//        Toast.makeText(this, ""+msgDate, Toast.LENGTH_SHORT).show();
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    public void toogleLayout(View view) {
        circle.setVisibility(View.GONE);
        ImageView imageView = (ImageView) view;
        if (peerLayout.getVisibility() == View.VISIBLE){
            peerLayout.setVisibility(View.GONE);
            chatLayout.setVisibility(View.VISIBLE);
            imageView.setImageDrawable(getDrawable(R.drawable.baseline_people_24));
        } else {
            peerLayout.setVisibility(View.VISIBLE);
            chatLayout.setVisibility(View.GONE);
            imageView.setImageDrawable(getDrawable(R.drawable.baseline_chat_bubble_24));
        }
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

    @SuppressLint("UseCompatLoadingForDrawables")
    public void playAndPause(boolean isPlaying){
        if (isPlaying){
            player.pause();
            playPause.setImageDrawable(getDrawable(R.drawable.exo_play));
        }else {
            player.play();
            playPause.setImageDrawable(getDrawable(R.drawable.exo_pause));
        }
    }

    public void test(View view) {
//        long ms = player.getCurrentPosition();
//        ms += 10000;
//        if (player.getDuration() - 5000 > ms){
//            player.seekTo(ms);
//        }
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

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
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
        }
        else {
            fullscreen.setImageResource(R.drawable.fullscreen);
            hideLayout(false);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                getWindow().getAttributes().layoutInDisplayCutoutMode =
                        WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT;
            }
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
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

//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                    getWindow().getAttributes().layoutInDisplayCutoutMode =
//                            WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
//                }
        playerLayout.setLayoutParams(lp);
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
        playerView.setVisibility(View.VISIBLE);
        playVideo(videoUri);
//        CallService.listener.onSendPlaybackStateRequest();
    }


    public void refreshLayout(View view) {
        releasePlayer();
        playerView.setVisibility(View.GONE);
        addMediaLayout.setVisibility(View.VISIBLE);
    }

    private void releasePlayer() {
        if (player != null){
            player.release();
        }
        if (playerView != null){
            playerView.setPlayer(null);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //listener = null;
        releasePlayer();
        if (!eventListenerList.isEmpty()){
            for (EventListenerData listenerData: eventListenerList) {
                listenerData.getDatabaseReference().removeEventListener(listenerData.getValueEventListener());
            }
        }
        //FirebaseUtils.removeUserData(room.getCode(),room.getUser());
        if (CallService.listener == null){
            return;
        }

        //CallService.listener.onDisconnect();
    }
}