package com.github.jaykkumar01.watchparty.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.github.jaykkumar01.watchparty.R;
import com.github.jaykkumar01.watchparty.assets.Complex;
import com.github.jaykkumar01.watchparty.assets.FFT;
import com.github.jaykkumar01.watchparty.interfaces.Data;

import java.util.Random;

public class Base{

    public static String stringToString(Object... items) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < items.length; i++) {
            Object item = items[i];
            if (item instanceof String) {
                result.append("\"").append(item).append("\"");
            } else {
                result.append(item);
            }
            // Separate items with a comma if it's not the last item
            if (i < items.length - 1) {
                result.append(",");
            }
        }
        return result.toString();
    }


    public static boolean isNetworkAvailable(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
    public static String generateRandomString() {
        // Generate a random string
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        int length = 15;
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }
        return sb.toString();
    }



    public static String generateRandomRoomCode(Activity activity) {
        int randomNumber = new Random().nextInt(1000000);
        return String.format(activity.getString(R.string._06d), randomNumber);
    }

    public static String formatSeconds(int seconds) {
        int hours = seconds/3600;
        int minutes = (seconds % 3600) / 60;
        int remainingSeconds = seconds % 60;
        if (hours > 0){
            return pad(hours) + ':' + pad(minutes) + ':' + pad(remainingSeconds);
        } else {
            return pad(minutes) + ':' + pad(remainingSeconds);
        }
    }

    @SuppressLint("DefaultLocale")
    private static String pad(int hours) {
        return String.format("%02d",hours);
    }

    public static float getZoomFactor(float value) {
        int dpi = Resources.getSystem().getDisplayMetrics().densityDpi;
        int deviceWidth = Resources.getSystem().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT
                ? Resources.getSystem().getDisplayMetrics().heightPixels : Resources.getSystem().getDisplayMetrics().widthPixels;

        return (float) (value * (double) deviceWidth / dpi * 0.18608773);
    }




}
