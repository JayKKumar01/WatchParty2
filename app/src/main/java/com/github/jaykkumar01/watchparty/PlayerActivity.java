package com.github.jaykkumar01.watchparty;

import static com.google.android.exoplayer2.Player.REPEAT_MODE_ONE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.jaykkumar01.watchparty.adapters.ChatAdapter;
import com.github.jaykkumar01.watchparty.adapters.UserAdapter;
import com.github.jaykkumar01.watchparty.models.EventListenerData;
import com.github.jaykkumar01.watchparty.models.MessageModel;
import com.github.jaykkumar01.watchparty.models.Room;
import com.github.jaykkumar01.watchparty.models.UserModel;
import com.github.jaykkumar01.watchparty.services.CallService;
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
    private List<MessageModel> chatList = new ArrayList<>();
    private ChatAdapter chatAdapter;

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
        codeTV = findViewById(R.id.roomCode);
        userNameTV = findViewById(R.id.userName);
        userListRV = findViewById(R.id.recyclerViewUsers);
        setupUsersRecycleView(userListRV);
        chatListRV = findViewById(R.id.recyclerViewChats);
        setupChatsRecycleView(chatListRV);

        codeTV.setText(room.getCode());
        userNameTV.setText(room.getUser().getUserId());

        messageET = findViewById(R.id.messageTXT);

    }

    private void setupChatsRecycleView(RecyclerView chatListRV) {
        chatListRV.setLayoutManager(new LinearLayoutManager(this));
        chatAdapter = new ChatAdapter(this,chatList);
        chatListRV.setAdapter(chatAdapter);


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

    private void startAgora() {
        Intent serviceIntent = new Intent(this, CallService.class);
        serviceIntent.putExtra(getString(R.string.room),room);
        startService(serviceIntent);
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
        String message = messageET.getText().toString();
        MessageModel messageModel = new MessageModel(room.getUser().getUserId(),message);
        messageModel.setName(room.getUser().getName());
        CallService.listener.sendMessage(messageModel);
        messageET.setText("");
    }



}