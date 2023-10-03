package com.github.jaykkumar01.watchparty.utils;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.res.Configuration;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.github.jaykkumar01.watchparty.R;

public class ChatUtil {
    Context context;
    ConstraintLayout playerLayout;
    ConstraintLayout partyLayout;
    ConstraintLayout parentLayout;
    ImageView imgChat;
    ImageView liveClose;

    public ChatUtil(Context context) {
        this.context = context;
    }

    public void setImgChat(ImageView imgChat) {
        this.imgChat = imgChat;
    }

    public void setLiveClose(ImageView liveClose) {
        this.liveClose = liveClose;
    }

    public void setPlayerLayout(ConstraintLayout playerLayout) {
        this.playerLayout = playerLayout;
    }

    public void setPartyLayout(ConstraintLayout partyLayout) {
        this.partyLayout = partyLayout;
    }

    public void setParentLayout(ConstraintLayout parentLayout) {
        this.parentLayout = parentLayout;
    }

    public void activate(boolean isTap){
        ConstraintLayout.LayoutParams playerLP = (ConstraintLayout.LayoutParams) playerLayout.getLayoutParams();
        if (isTap){
            playerLP.matchConstraintPercentWidth = .6f;
            imgChat.setImageResource(R.drawable.chat_on);
            ConstraintSet set = new ConstraintSet();
            set.clone(parentLayout);
            set.connect(partyLayout.getId(),ConstraintSet.LEFT,playerLayout.getId(),ConstraintSet.RIGHT);
            set.connect(partyLayout.getId(),ConstraintSet.TOP, ConstraintSet.PARENT_ID,ConstraintSet.TOP);
            set.applyTo(parentLayout);
            partyLayout.setVisibility(View.VISIBLE);
        }else{
            playerLP.matchConstraintPercentWidth = 1f;
            imgChat.setVisibility(View.VISIBLE);
            liveClose.setVisibility(View.VISIBLE);
            imgChat.setImageResource(R.drawable.chat_off);
            partyLayout.setVisibility(View.GONE);
        }
        playerLayout.setLayoutParams(playerLP);
    }
    public void disable(boolean isTap){
        ConstraintLayout.LayoutParams playerLP = (ConstraintLayout.LayoutParams) playerLayout.getLayoutParams();
        playerLP.matchConstraintPercentWidth = 1f;
        playerLayout.setLayoutParams(playerLP);
        if (isTap){
            partyLayout.setVisibility(View.GONE);
            imgChat.setVisibility(View.VISIBLE);
            liveClose.setVisibility(View.VISIBLE);
            imgChat.setImageResource(R.drawable.chat_off);
        }else {
            imgChat.setVisibility(View.GONE);
            liveClose.setVisibility(View.GONE);
            ConstraintSet set = new ConstraintSet();
            set.clone(parentLayout);
            set.connect(partyLayout.getId(),ConstraintSet.LEFT,ConstraintSet.PARENT_ID,ConstraintSet.LEFT);
            set.connect(partyLayout.getId(),ConstraintSet.TOP, playerLayout.getId(),ConstraintSet.BOTTOM);
            set.applyTo(parentLayout);
            partyLayout.setVisibility(View.VISIBLE);
        }
    }




}
