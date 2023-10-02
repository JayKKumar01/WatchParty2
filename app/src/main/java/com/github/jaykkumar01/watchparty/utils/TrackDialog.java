package com.github.jaykkumar01.watchparty.utils;
import android.app.Dialog;
import android.content.Context;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.TrackSelectionDialogBuilder;

public class TrackDialog {

    private Dialog audDialog,vidDialog,subDialog;
    private final Context context;
    private final ExoPlayer player;
    private final TrackSelector trackSelector;

    public TrackDialog(Context context, ExoPlayer player, TrackSelector trackSelector) {
        this.context = context;
        this.player = player;
        this.trackSelector = trackSelector;
    }
    public void changeAudio(){
        if(audDialog == null){

            TrackSelectionDialogBuilder trackSelectionDialogBuilder = new TrackSelectionDialogBuilder(context, "AUDIO TRACKS", player,
                    C.TRACK_TYPE_AUDIO);
            trackSelectionDialogBuilder.setAllowAdaptiveSelections(true);
            trackSelectionDialogBuilder.setShowDisableOption(true);

            audDialog = trackSelectionDialogBuilder.build();
            audDialog.show();
        }else {
            audDialog.show();
        }
    }

    public void changeVideo(){
        if(vidDialog == null){
            TrackSelectionDialogBuilder trackSelectionDialogBuilder = new TrackSelectionDialogBuilder(context, "VIDEO TRACKS", player,
                    C.TRACK_TYPE_VIDEO);
            trackSelectionDialogBuilder.setAllowAdaptiveSelections(true);
            trackSelectionDialogBuilder.setShowDisableOption(true);
            vidDialog = trackSelectionDialogBuilder.build();
            vidDialog.show();
        }else {
            vidDialog.show();
        }
    }
    public void changeSubtitle(){
        if(subDialog == null){
            TrackSelectionDialogBuilder trackSelectionDialogBuilder = new TrackSelectionDialogBuilder(context, "SUBTITLE TRACKS", player,
                    C.TRACK_TYPE_TEXT);
            trackSelectionDialogBuilder.setAllowAdaptiveSelections(true);
            trackSelectionDialogBuilder.setShowDisableOption(false);
            subDialog = trackSelectionDialogBuilder.build();
            subDialog.show();
        }else {
            subDialog.show();
        }
    }
}
