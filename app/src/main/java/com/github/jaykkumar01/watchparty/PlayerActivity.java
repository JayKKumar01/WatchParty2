package com.github.jaykkumar01.watchparty;

import static com.google.android.exoplayer2.Player.REPEAT_MODE_ONE;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.jaykkumar01.watchparty.enums.RoomType;
import com.github.jaykkumar01.watchparty.interfaces.FirebaseListener;
import com.github.jaykkumar01.watchparty.models.EventListenerData;
import com.github.jaykkumar01.watchparty.models.ListenerData;
import com.github.jaykkumar01.watchparty.models.Room;
import com.github.jaykkumar01.watchparty.models.UserModel;
import com.github.jaykkumar01.watchparty.services.CallService;
import com.github.jaykkumar01.watchparty.utils.FirebaseUtils;
import com.github.jaykkumar01.watchparty.utils.Menu;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class PlayerActivity extends AppCompatActivity {
    Room room;
    TextView codeTV;
    List<EventListenerData> eventListenerList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().getAttributes().layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
        setContentView(R.layout.activity_player);
        codeTV = findViewById(R.id.codeTV);
        room = (Room) getIntent().getSerializableExtra(getString(R.string.room));
        if (room == null){
            return;
        }
        codeTV.setText(room.getCode());

        if (room.getRoomType() == RoomType.JOINED){
            EventListenerData listenerData = FirebaseUtils.getUserList(room.getCode(), (successful, data) -> {
                if (successful){
                    StringJoiner joiner = new StringJoiner("\n");
                    List<UserModel> list = data.getUserList();
                    for (UserModel userModel: list){
                        joiner.add(userModel.getUserId());
                    }
                    codeTV.setText(joiner.toString());
                    if (list.isEmpty()){
                        Toast.makeText(PlayerActivity.this, data.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(PlayerActivity.this, data.getErrorMessage()+"", Toast.LENGTH_SHORT).show();
                }
            });
            eventListenerList.add(listenerData);
        }

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
    }
}