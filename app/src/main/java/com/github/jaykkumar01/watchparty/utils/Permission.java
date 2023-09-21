package com.github.jaykkumar01.watchparty.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Permission {
    private static final int REQ_CODE = 100;

    private static final String[] PERMISSIONS;

    static {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            PERMISSIONS = new String[]{
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.POST_NOTIFICATIONS
            };
        } else {
            PERMISSIONS = new String[]{
                    Manifest.permission.RECORD_AUDIO
            };
        }
    }

    public static void askPermission(Context context) {
        if (isGranted(context)){
            createConfigFile(context,false);
            return;
        }
        if (isDeniedPermanently(context)){
            showGoToSettingsDialog(context);
        }
        else if (isRational((Activity) context)) {
            showRationaleDialog(context);
        } else {
            ActivityCompat.requestPermissions((Activity) context, PERMISSIONS, REQ_CODE);
        }
    }

    private static boolean isRational(Activity activity) {
        for (String permission: PERMISSIONS){
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity,permission)){
                return false;
            }
        }
        return true;
    }

    private static boolean isDeniedPermanently(Context context) {
        File configFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "permissionConfig.txt");
        return configFile.exists() && !isRational((Activity) context);
    }
    public static boolean isGranted(Context context){
        for(String permission: PERMISSIONS){
            if(ActivityCompat.checkSelfPermission(context,permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }



    private static void showRationaleDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Allow Permission");
        builder.setMessage("This app requires certain permissions.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                createConfigFile(context,true);
                // Request permissions again
                ActivityCompat.requestPermissions((Activity) context, PERMISSIONS, REQ_CODE);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Show dialog to go to settings
                Toast.makeText(context, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

    private static void createConfigFile(Context context, boolean create) {
        File configFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "permissionConfig.txt");
        if (!create && configFile.exists()) {
            configFile.delete();
            return;
        }
        String txt = "Permission Denied Permanently";
        try (FileOutputStream outputStream = new FileOutputStream(configFile)) {
            outputStream.write(txt.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void showGoToSettingsDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Permission Required");
        builder.setMessage("This app requires permissions to function properly. Please go to Settings and grant the necessary permissions.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Open app settings
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                intent.setData(uri);
                context.startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle user cancelation
                Toast.makeText(context, "Permission denied.", Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }
}
