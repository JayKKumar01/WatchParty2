package com.github.jaykkumar01.watchparty.helpers;

import static androidx.media3.common.Player.REPEAT_MODE_ONE;

import android.app.Activity;
import android.app.PictureInPictureParams;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.util.Rational;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.OptIn;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackParameters;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector;
import androidx.media3.exoplayer.trackselection.TrackSelector;
import androidx.media3.ui.PlayerView;

import com.github.jaykkumar01.watchparty.PlayerActivity;
import com.github.jaykkumar01.watchparty.R;
import com.github.jaykkumar01.watchparty.assets.TrackSelectionDialog;
import com.github.jaykkumar01.watchparty.interfaces.PlayerListener;
import com.github.jaykkumar01.watchparty.services.CallService;
import com.github.jaykkumar01.watchparty.utils.PlayerUtil;
import com.github.jaykkumar01.watchparty.utils.TouchGesture;

public class PlayerManagement implements View.OnClickListener {

    @Override
    public void onClick(View view) {
        if (view == playPause){
            playAndPause();
        } else if (view == muteUnmute) {
            muteUnmute();
        } else if (view == imgCC) {
            CC();
        } else if (view == fullScreen) {
            fullScreen();
        } else if (view == lock) {
            ctrlLayout.setVisibility(View.GONE);
            bigLock.setVisibility(View.VISIBLE);
        } else if (view == bigLock){
            bigLock.setVisibility(View.GONE);
            ctrlLayout.setVisibility(View.VISIBLE);
        } else if (view == gear) {
            changeVideo();
        } else if (view == pip) {
            enterPIP();
        } else if (view == speed) {
            changeSpeed();
        } else if (view == imgChat){
            PlayerActivity.listener.onChatClick();
        }


    }

    public interface Listener{
        void onSeekInfo(String id, long positionMs);
        void onPlayPauseInfo(String id, boolean isPlaying);
        void onPlaybackStateRequest(String id);
        void onPlaybackStateReceived(String id, boolean isPlaying, long positionMs);

        void onConfigChange(Configuration newConfig);
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
    private ImageView playPause,muteUnmute,imgCC,fullScreen,lock,bigLock,gear,pip,speed;
    private ConstraintLayout ctrlLayout,rootCtrlLayout;
    private ImageView imgChat;

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
            public void onConfigChange(Configuration newConfig) {
                if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
                    fullScreen.setImageResource(R.drawable.fullscreen_exit);
                }else{
                    fullScreen.setImageResource(R.drawable.fullscreen);
                }

            }

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
        fullScreen = findView(R.id.exo_screen);
        ctrlLayout = findView(R.id.ctrlLayout);
        rootCtrlLayout = findView(R.id.root_exo_layout);
        lock = findView(R.id.exo_lock);
        bigLock = findView(R.id.big_lock);
        gear = findView(R.id.exo_vidTrack);
        pip = findView(R.id.exo_pip);
        speed = findView(R.id.exo_speed);
        imgChat = findView(R.id.exo_chat);

        setOnClickListenerToImageViews(rootCtrlLayout,this);
    }

    public void setOnClickListenerToImageViews(View view, View.OnClickListener listener) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                if (child instanceof ImageView) {
                    ImageView imageView = (ImageView) child;
                    imageView.setOnClickListener(listener);
                }
                if (child instanceof ViewGroup) {
                    setOnClickListenerToImageViews(child, listener);
                }
            }
        }
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

    public void enterPIP(){
        Display d = activity.getWindowManager().getDefaultDisplay();
        Point p = new Point();
        d.getSize(p);
        Rational ratio = new Rational(16,9);
        //ratio = new Rational(dimension(vidUri)[0],dimension(vidUri)[1]);
        PictureInPictureParams.Builder pipBuilder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            hideController();

            pipBuilder = new PictureInPictureParams.Builder();
            pipBuilder.setAspectRatio(ratio).build();
            activity.enterPictureInPictureMode(pipBuilder.build());
            PlayerActivity.listener.onPip();
        }

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
