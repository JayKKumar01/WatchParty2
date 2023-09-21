package com.github.jaykkumar01.watchparty.interfaces;

import android.location.GnssAntennaInfo;

import com.github.jaykkumar01.watchparty.models.ListenerData;

public interface FirebaseListener {
    void onComplete(boolean successful, ListenerData data);
}
