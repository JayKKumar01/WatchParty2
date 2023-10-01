package com.github.jaykkumar01.watchparty.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.activity.result.ActivityResultLauncher;

public class PickerUtil{
    public static void pickVideo(ActivityResultLauncher<Intent> pickVideoLauncher){

        Intent data = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        //data.setType("*/*");
        data.setType("video/*");
        data.addCategory(Intent.CATEGORY_OPENABLE);
        data = Intent.createChooser(data,"Video Picker");
        pickVideoLauncher.launch(data);
    }
}
