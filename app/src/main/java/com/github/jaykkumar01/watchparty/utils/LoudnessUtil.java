package com.github.jaykkumar01.watchparty.utils;

import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.github.jaykkumar01.watchparty.interfaces.LoudnessListener;

public class LoudnessUtil {


    private final View view;
    LoudnessListener listener;
    private final ConstraintLayout.LayoutParams params;

    public LoudnessUtil(View view) {
        this.view = view;
        params = (ConstraintLayout.LayoutParams)view.getLayoutParams();

    }

    public LoudnessListener getListener() {
        return listener;
    }

    public void setListener(LoudnessListener listener) {
        this.listener = listener;
    }

    public void resizeHeight(float loudness) {
        params.matchConstraintPercentHeight = loudness;
        view.setLayoutParams(params);
    }
}
