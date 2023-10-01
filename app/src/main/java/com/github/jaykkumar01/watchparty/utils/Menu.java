package com.github.jaykkumar01.watchparty.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.github.jaykkumar01.watchparty.R;

public class Menu {
    //Menu.changeMenu(this,true);
    Context context;
    Activity activity;
    ConstraintLayout menu,bgMenu;
    public static boolean once;

    public Menu() {
    }

    public Menu(Context context) {
        this.context = context;
        this.activity = (Activity) context;
//        this.menu = activity.findViewById(R.id.menu);
//        this.bgMenu = activity.findViewById(R.id.bgMenu);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public static void changeBGMenu(Context context,ConstraintLayout bgMenu, boolean isLandscape){
        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) bgMenu.getLayoutParams();
        if (isLandscape){
            lp.matchConstraintPercentWidth = .5f;
            lp.matchConstraintPercentHeight = 1f;
            lp.horizontalBias = 0f;
            lp.verticalBias = .5f;
            bgMenu.setBackground(context.getDrawable(R.drawable.bg_menu));
        }
        else{
            lp.matchConstraintPercentWidth = 1f;
            lp.matchConstraintPercentHeight = .5f;
            lp.horizontalBias = .5f;
            lp.verticalBias = 0f;
            bgMenu.setBackground(context.getDrawable(R.drawable.bg_menu_portrait));
        }
        bgMenu.setLayoutParams(lp);

    }
    public static void changeMenu(Context context, boolean isLandscape){
//        Toast.makeText(context, isLandscape+"", Toast.LENGTH_SHORT).show();
        Menu obj = new Menu(context);
        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) obj.menu.getLayoutParams();
        if (isLandscape){
            lp.matchConstraintPercentWidth = .5f;
            lp.matchConstraintPercentHeight = 1f;
            lp.horizontalBias = 1f;
            lp.verticalBias = .5f;
        }
        else{
            lp.matchConstraintPercentWidth = 1f;
            lp.matchConstraintPercentHeight = .5f;
            lp.horizontalBias = .5f;
            lp.verticalBias = 1f;
        }
        obj.menu.setLayoutParams(lp);
        changeBGMenu(context,obj.bgMenu,isLandscape);
    }
    public static void setMenu(Context context){
        once = false;
        Menu obj = new Menu(context);
        if (obj.context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            changeMenu(context,false);
        }
        else {
            changeMenu(context,true);
        }
    }
//    public static void showMenu(Context context){
//        Menu obj = new Menu(context);
//        StyledPlayerView playerView = obj.activity.findViewById(R.id.player_view);
//        if(obj.menu.getVisibility() == View.VISIBLE){
//            return;
//        }
//        playerView.hideController();
//        obj.menu.setTranslationX(0);
//        obj.menu.setTranslationY(0);
//        obj.bgMenu.setTranslationX(0);
//        obj.bgMenu.setTranslationY(0);
//
//        if (!once){
//            showAnimateOnce(obj.menu);
//            showAnimateOnce(obj.bgMenu);
//
//            once = true;
//            return;
//        }
//        showAnimate(obj.menu);
//        showAnimate(obj.bgMenu);
//
//    }

    public static void hideMenu(Context context){
        Menu obj = new Menu(context);
        if(obj.menu.getVisibility() != View.VISIBLE){
            return;
        }
        hideAnimate(obj.menu);
        hideAnimate(obj.bgMenu);

    }

    public static boolean isLandscape(){
        if (Resources.getSystem().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            return true;
        }
        return false;
    }
    public static boolean isVisible(Context context){
        Menu obj = new Menu(context);
        if(obj.menu.getVisibility() == View.VISIBLE){
            return true;
        }
        return false;
    }
    public static void showAnimateOnce(ConstraintLayout layout){
        layout.setVisibility(View.INVISIBLE);
        layout.animate().translationX(0).setDuration(0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (isLandscape()){
                    layout.setTranslationX(layout.getWidth());
                }
                else {
                    layout.setTranslationY(layout.getHeight());
                }
                layout.setVisibility(View.VISIBLE);
                layout.animate().translationX(0).translationY(0).setDuration(300).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        //
                    }
                });
            }
        });
    }
    public static void showAnimate(ConstraintLayout layout){
        if (isLandscape()){
            layout.setTranslationX(layout.getWidth());
        }
        else {
            layout.setTranslationY(layout.getHeight());
        }
        layout.setVisibility(View.VISIBLE);
        layout.animate().translationX(0).translationY(0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
            }
        });
    }
    public static void hideAnimate(ConstraintLayout layout){
        if (isLandscape()){
            layout.animate().translationX(layout.getWidth()).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    layout.setVisibility(View.GONE);
                }
            });
        }
        else {
            layout.animate().translationY(layout.getHeight()).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    layout.setVisibility(View.GONE);
                }
            });
        }
    }
}