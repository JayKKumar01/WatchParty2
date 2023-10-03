package com.github.jaykkumar01.watchparty.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import com.github.jaykkumar01.watchparty.R;

@SuppressLint("UnsafeOptInUsageError")
 public class TapTap extends GestureDetector.SimpleOnGestureListener {
    private static final int TIMEOUT = 600;
    private final Context context;
    private final Activity activity;
    private final ExoPlayer player;
    private final PlayerView playerView;
    View ar1,ar2,ar3,lar1,lar2,lar3,leftBox,rightBox;
    TextView rightTXT,leftTXT;
    public static int seekVal = 0;
    private boolean inAnimation;
    private static long curTimeR = System.currentTimeMillis();
    private static long curTimeL = System.currentTimeMillis();
    Handler handler = new Handler();
    private static int prevSide = 0;

    public TapTap(Context context, PlayerView playerView, ExoPlayer player) {
        this.context = context;
        activity = (Activity)context;
        this.playerView = playerView;
        this.player = player;
        initViews();
    }

    private void initViews() {
        ar1 = activity.findViewById(R.id.arrow1);
        ar2 = activity.findViewById(R.id.arrow2);
        ar3 = activity.findViewById(R.id.arrow3);
        lar1 = activity.findViewById(R.id.Larrow1);
        lar2 = activity.findViewById(R.id.Larrow2);
        lar3 = activity.findViewById(R.id.Larrow3);
        leftBox = activity.findViewById(R.id.leftBox);
        rightBox = activity.findViewById(R.id.rightBox);
        rightTXT = activity.findViewById(R.id.rightSeekTXT);
        leftTXT = activity.findViewById(R.id.leftSeekTXT);
    }


    @Override
    public boolean onSingleTapConfirmed(@NonNull MotionEvent event) {
        if (playerView.isControllerFullyVisible()){
            playerView.hideController();
        }else {
            playerView.showController();
            MyHandler.hideControls(playerView);
        }
        return super.onSingleTapConfirmed(event);
    }

    @Override
    public boolean onDoubleTap(@NonNull MotionEvent e) {

        int seekAmount = 10 * 1000; // Seek amount in milliseconds

        if (e.getX() < (double) playerView.getWidth() / 2) {
            seek(-seekAmount, lar1, lar2, lar3, leftTXT, leftBox);
        } else {
            seek(seekAmount, ar1, ar2, ar3, rightTXT, rightBox);
        }




        return super.onDoubleTap(e);
    }
    private void seek(int seekAmount, View ar1, View ar2, View ar3, TextView txt, View box) {
        long position = player.getCurrentPosition() + seekAmount;
        boolean isLeft = seekAmount < 0;
        int absSeekValue = seekAmount / 1000;

        if (position <= player.getDuration() && position >= 0) {

            if ((isLeft && prevSide > 0) || (!isLeft && prevSide < 0)){
                seekVal = 0;
            }
            prevSide = isLeft ? -1 : 1;

            player.seekTo(position);
            seekVal += absSeekValue;
            runHandler(isLeft, box);
            forwardAnimation(box, ar1, ar2, ar3, txt);
        } else {
            box.setVisibility(View.GONE);
            seekVal = 0;
            prevSide = 0;
        }
    }



    private void runHandler(boolean isLeft, View box) {
        if (isLeft){
            curTimeL = System.currentTimeMillis();
        }else {
            curTimeR = System.currentTimeMillis();
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (seekVal != 0){

                    if (System.currentTimeMillis() - (isLeft ? curTimeL : curTimeR) > TIMEOUT) {
                        box.setVisibility(View.GONE);
                        if ((isLeft && prevSide < 0) || (!isLeft && prevSide > 0)){
                            seekVal = 0;
                        }
                    }else{
                        handler.postDelayed(this,TIMEOUT);
                    }
                }else {
                    box.setVisibility(View.GONE);
                }
            }
        },TIMEOUT);
    }


    @SuppressLint("SetTextI18n")
    private void forwardAnimation(View box, View ar1, View ar2, View ar3, TextView txt){

        box.setVisibility(View.VISIBLE);

        txt.setAlpha(0.1f);
        txt.setScaleX(1);
        txt.setScaleY(1);
        txt.setText(seekVal > 0 ? "+"+seekVal : seekVal+"");
        txt.setVisibility(View.VISIBLE);
        txt.animate().alpha(1.0f).scaleX(2).scaleY(2).setDuration(TIMEOUT).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
            }
        });

        if(inAnimation){
            return;
        }
        inAnimation = true;

        ar1.setAlpha(1.0f);
        ar2.setAlpha(0.2f);
        ar3.setAlpha(0.2f);

        ar1.animate().alpha(0.2f).setDuration(200).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
            }
        });
        ar2.animate().alpha(1.0f).setDuration(200).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                ar3.animate().alpha(1.0f).setDuration(200).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        ar3.animate().alpha(0.2f).setDuration(200).setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
//                                box.setVisibility(View.GONE);
                                inAnimation = false;
                            }
                        });
                    }
                });
                ar2.animate().alpha(0.2f).setDuration(200).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                    }
                });
            }
        });
    }
}
