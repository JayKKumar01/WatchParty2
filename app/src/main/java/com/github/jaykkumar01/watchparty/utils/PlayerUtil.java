package com.github.jaykkumar01.watchparty.utils;

import android.content.Context;
import android.widget.Toast;

import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;

import com.github.jaykkumar01.watchparty.interfaces.PlayerListener;

public class PlayerUtil {
    private Context context;
    private int preState = 2;
    private ExoPlayer.Listener listener;
    private ExoPlayer player;

    public PlayerUtil(Context context) {
        this.context = context;
    }

    public void addSeekListener(ExoPlayer player, PlayerListener playerListener){
        this.player = player;
        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                Player.Listener.super.onPlaybackStateChanged(playbackState);
                player.removeListener(this);
                playerListener.onPlayerReady();
            }
        });
        listener = new Player.Listener() {
            @Override
            public void onPositionDiscontinuity(Player.PositionInfo oldPosition, Player.PositionInfo newPosition, int reason) {
                Player.Listener.super.onPositionDiscontinuity(oldPosition, newPosition, reason);
                playerListener.onSeek(newPosition.positionMs);
            }

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                Player.Listener.super.onIsPlayingChanged(isPlaying);
                //int temp = preState;
                if (player.getPlaybackState() != Player.STATE_BUFFERING && preState != 2){
                    playerListener.onIsPlaying(isPlaying);
                }
                preState = player.getPlaybackState();
                //Toast.makeText(context, "prestate: "+temp+" "+preState, Toast.LENGTH_SHORT).show();

            }
        };
        player.addListener(listener);
    }

    public void unsetState(){
        preState = -1;
    }

    public void removeListener(){
        if (player != null && listener != null){
            player.removeListener(listener);
        }
    }
}
