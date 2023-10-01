package com.github.jaykkumar01.watchparty.utils;

import com.github.jaykkumar01.watchparty.interfaces.PlayerListener;
import com.google.android.exoplayer2.Player;

public class PlayerUtil {
    private static int preState = 2;

    public static void addSeekListener(Player player, PlayerListener playerListener){
        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                Player.Listener.super.onPlaybackStateChanged(playbackState);
                player.removeListener(this);
                playerListener.onPlayerReady();
            }
        });
        player.addListener(new Player.Listener() {
            @Override
            public void onPositionDiscontinuity(Player.PositionInfo oldPosition, Player.PositionInfo newPosition, int reason) {
                Player.Listener.super.onPositionDiscontinuity(oldPosition, newPosition, reason);
                playerListener.onSeek(newPosition.positionMs);
            }

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                Player.Listener.super.onIsPlayingChanged(isPlaying);
                if (player.getPlaybackState() != Player.STATE_BUFFERING && preState != 2){
                    playerListener.onIsPlaying(isPlaying);
                }
                preState = player.getPlaybackState();
            }
        });

    }
}
