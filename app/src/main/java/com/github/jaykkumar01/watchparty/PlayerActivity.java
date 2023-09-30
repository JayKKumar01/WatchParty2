package com.github.jaykkumar01.watchparty;

import static com.google.android.exoplayer2.Player.REPEAT_MODE_ONE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.jaykkumar01.watchparty.adapters.ChatAdapter;
import com.github.jaykkumar01.watchparty.adapters.UserAdapter;
import com.github.jaykkumar01.watchparty.interfaces.PlayerActivityListener;
import com.github.jaykkumar01.watchparty.models.EventListenerData;
import com.github.jaykkumar01.watchparty.models.MessageModel;
import com.github.jaykkumar01.watchparty.models.Room;
import com.github.jaykkumar01.watchparty.models.UserModel;
import com.github.jaykkumar01.watchparty.services.CallService;
import com.github.jaykkumar01.watchparty.update.Info;
import com.github.jaykkumar01.watchparty.utils.FirebaseUtils;
import com.github.jaykkumar01.watchparty.utils.Menu;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
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
        setUpListener();
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



    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Menu.changeMenu(this, newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE);
    }


    public void playVideo(View view) {



        StyledPlayerView playerView = findViewById(R.id.player_view);
        ExoPlayer player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);
        MediaItem mediaItem = new MediaItem.Builder()
                .setUri("/storage/emulated/0/CricHunt/index.mp4")
                .build();
        //        player.addListener(new PlayerEventListener());
        player.setMediaItem(mediaItem);
//        player.setAudioAttributes(mPlaybackAttributes, true);
        player.prepare();
        player.setRepeatMode(REPEAT_MODE_ONE);
        player.play();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!eventListenerList.isEmpty()){
            for (EventListenerData listenerData: eventListenerList) {
                listenerData.getDatabaseReference().removeEventListener(listenerData.getValueEventListener());
            }
        }
        FirebaseUtils.removeUserData(room.getCode(),room.getUser());
        CallService.listener.onDisconnect();
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
        toogleMic(true);
    }
    public void deafen(View view) {
        toogleDeafen(true);
    }
    private void toogleMic(boolean isTap) {
        Info.isMute = !Info.isMute;
        if (isTap){
            CallService.listener.onToogleMic();
        }
        setMicImage();
    }
    private void toogleDeafen(boolean isTap) {
        Info.isDeafen = !Info.isDeafen;
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
}