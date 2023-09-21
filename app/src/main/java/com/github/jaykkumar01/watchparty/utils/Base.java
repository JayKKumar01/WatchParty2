package com.github.jaykkumar01.watchparty.utils;

import android.app.Activity;

import com.github.jaykkumar01.watchparty.R;

import java.util.Random;

public class Base {
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
}
