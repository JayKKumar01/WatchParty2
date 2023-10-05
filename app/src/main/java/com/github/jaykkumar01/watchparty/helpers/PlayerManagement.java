package com.github.jaykkumar01.watchparty.helpers;

import static androidx.media3.common.Player.REPEAT_MODE_ONE;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.OptIn;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;
import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackParameters;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector;
import androidx.media3.exoplayer.trackselection.TrackSelector;
import androidx.media3.ui.PlayerView;

import com.github.jaykkumar01.watchparty.R;
import com.github.jaykkumar01.watchparty.assets.TrackSelectionDialog;
import com.github.jaykkumar01.watchparty.interfaces.PlayerListener;
import com.github.jaykkumar01.watchparty.services.CallService;
import com.github.jaykkumar01.watchparty.utils.PlayerUtil;
import com.github.jaykkumar01.watchparty.utils.TouchGesture;

public class PlayerManagement {

    public interface Listener{
        void onSeekInfo(String id, long positionMs);
        void onPlayPauseInfo(String id, boolean isPlaying);
        void onPlaybackStateRequest(String id);
        void onPlaybackStateReceived(String id, boolean isPlaying, long positionMs);
    }

    public static Listener listener;
    private PlayerUtil playerUtil;
    private boolean isFirstSync;
    private int playbackSpeed = 2;
    private boolean isListenerCommand;
    private boolean isShowingTrackSelectionDialog;
    private boolean isPartyStopped;
    private final Context context;
    private final Activity activity;
    private PlayerView playerView;
    private ExoPlayer player;
    private final FragmentManager fragmentManager;
    private ImageView playPause,muteUnmute,imgCC;

    public static void start(Context context){
        new PlayerManagement(context,null);
    }

    public PlayerManagement(Context context,FragmentManager fragmentManager) {
        this.context = context;
        this.activity = (Activity)context;
        this.fragmentManager = fragmentManager;
        initViews();
        listener = setListener();
    }

    private Listener setListener() {
        return new Listener() {

            @Override
            public void onSeekInfo(String id, long positionMs) {
                isListenerCommand = true;
                if (player == null){
                    return;
                }

                activity.runOnUiThread(() -> {

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
                activity.runOnUiThread(() -> {
                    playAndPause(!isPlaying);
                });
            }

            @Override
            public void onPlaybackStateRequest(String id) {
                if (player == null){
                    return;
                }
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CallService.listener.onSendPlaybackState(id,player.isPlaying(),player.getCurrentPosition());
                    }
                });
            }

            @Override
            public void onPlaybackStateReceived(String id, boolean isPlaying, long positionMs) {
                if (!isFirstSync){
                    return;
                }
                if (player == null){
                    return;
                }
                activity.runOnUiThread(() -> {
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
        };
    }

    @OptIn(markerClass = UnstableApi.class)
    private void  initViews() {
        playerView = findView(R.id.player_view);
        TrackSelector trackSelector = new DefaultTrackSelector(context);
        playPause = findView(R.id.play_pause);
        muteUnmute = findView(R.id.exo_mute_unmute);
        imgCC = findView(R.id.exo_caption);
    }

    public void fullScreen() {
        activity.setRequestedOrientation(
                context.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE
                        ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        );
    }

    public void playAndPause() {
        playAndPause(player.isPlaying());
    }
    public void changeVideo() {
        if (!isShowingTrackSelectionDialog && TrackSelectionDialog.willHaveContent(player)) {
            isShowingTrackSelectionDialog = true;
            TrackSelectionDialog trackSelectionDialog =
                    TrackSelectionDialog.createForPlayer(
                            player,
                            /* onDismissListener= */ dismissedDialog -> isShowingTrackSelectionDialog = false);
            trackSelectionDialog.show(fragmentManager, /* tag= */ null);
        }
    }
    public void changeSpeed() {
        if (player == null){
            return;
        }
        AlertDialog.Builder playbackDialog = new AlertDialog.Builder(context);
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

    public void releasePlayer() {
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
    @OptIn(markerClass = UnstableApi.class)
    public void CC() {
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

    public void muteUnmute() {
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

    @OptIn(markerClass = UnstableApi.class)
    public void  hideController() {
        playerView.hideController();
    }


    @OptIn(markerClass = UnstableApi.class)
    public void playVideo(Uri videoUri) {
        playerUtil = new PlayerUtil(context);
        isFirstSync = true;
        player = new ExoPlayer.Builder(context)
                .build();
        playerView.setPlayer(player);
        playerView.setControllerAutoShow(false);
        MediaItem mediaItem = new MediaItem.Builder()
                .setUri(videoUri)
                .build();
        player.setMediaItem(mediaItem);
        player.prepare();
        player.setRepeatMode(REPEAT_MODE_ONE);
        player.play();
        playerView.onResume();

        resetPlayerViews();
        playerView.setOnTouchListener(new TouchGesture(context, playerView, player));

        playerUtil.addSeekListener(player, new PlayerListener() {
            @Override
            public void onSeek(long positionMs) {
                if (isListenerCommand){
                    isListenerCommand = false;
                    return;
                }
                activity.runOnUiThread(() -> CallService.listener.onSendSeekInfo(positionMs));

            }

            @Override
            public void onIsPlaying(boolean isPlaying) {
                if (isListenerCommand){
                    isListenerCommand = false;
                    return;
                }
                activity.runOnUiThread(() -> CallService.listener.onSendPlayPauseInfo(isPlaying));

            }

            @Override
            public void onPlayerReady() {
                activity.runOnUiThread(() -> CallService.listener.onSendPlaybackStateRequest());

            }
        });


    }

    private void resetPlayerViews() {
        playPause.setImageResource(R.drawable.exo_pause);
        playbackSpeed = 2;
        muteUnmute.setImageResource(R.drawable.volume_on);
        imgCC.setImageResource(R.drawable.cc_on);
    }

    private void playAndPause(boolean isPlaying){
        if (isPlaying){
            player.pause();
            playerUtil.unsetState();
            playPause.setImageResource(R.drawable.exo_play);
        }else {
            player.play();
            playPause.setImageResource(R.drawable.exo_pause);
        }
    }

    private <T extends View> T findView(int viewId) {
        View view = activity.findViewById(viewId);
        //noinspection unchecked
        return (T) view;
    }
}
