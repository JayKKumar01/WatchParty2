package com.github.jaykkumar01.watchparty.services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.github.jaykkumar01.watchparty.PlayerActivity;
import com.github.jaykkumar01.watchparty.R;
import com.github.jaykkumar01.watchparty.enums.RoomType;
import com.github.jaykkumar01.watchparty.interfaces.CallServiceListener;
import com.github.jaykkumar01.watchparty.interfaces.Data;
import com.github.jaykkumar01.watchparty.interfaces.JavaScriptInterface;
import com.github.jaykkumar01.watchparty.models.FileModel;
import com.github.jaykkumar01.watchparty.models.MessageModel;
import com.github.jaykkumar01.watchparty.models.Room;
import com.github.jaykkumar01.watchparty.models.UserModel;
import com.github.jaykkumar01.watchparty.receivers.NotificationReceiver;
import com.github.jaykkumar01.watchparty.update.Info;
import com.github.jaykkumar01.watchparty.utils.Base;
import com.github.jaykkumar01.watchparty.utils.FirebaseUtils;
import com.github.jaykkumar01.watchparty.utils.ObjectUtil;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CallService extends Service implements Data {

    private WebView webView;
    private Room room;
    private UserModel userModel;
    private NotificationManager notificationManager;

    public static CallServiceListener listener;
    Handler handler = new Handler();
    private AudioRecord audioRecord;
    private boolean isRecording;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    JavaScriptInterface javascriptInterface;

    List<MessageModel> messageModelList = new ArrayList<>();
    private PendingIntent mutePendingIntent, hangupPendingIntent, deafenPendingIntent;
    private NotificationCompat.Builder builder;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        setupListener();
        room = (Room) intent.getSerializableExtra(getString(R.string.room));
        userModel = room.getUser();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
        }

        audioRecord = new AudioRecord(
                MediaRecorder.AudioSource.VOICE_RECOGNITION,
                SAMPLE_RATE,
                CHANNEL_CONFIG,
                AUDIO_FORMAT,
                BUFFER_SIZE_IN_BYTES);


        createNotification();

        setupWebView();

        return START_NOT_STICKY;
    }

    private void stopRecording() {
        isRecording = false;
        if (audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
            audioRecord.stop();
        }
    }


    private void startRecording() {
        isRecording = true;
        audioRecord.startRecording();
        executorService.execute(new Runnable() {
            @Override
            public void run() {

                byte[] buffer = new byte[BUFFER_SIZE_IN_BYTES];
                while (isRecording) {

                    long millis = System.currentTimeMillis();
                    if (audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
                        continue;
                    }
                    int read = audioRecord.read(buffer, 0, BUFFER_SIZE_IN_BYTES);

                    FileModel file = new FileModel(buffer,read,millis);

                    if (!Base.isNetworkAvailable(CallService.this)) {
                        continue;
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callJavaScriptBytes("sendFile",file);
                        }
                    });
                }
            }
        });
    }

    private void setupListener() {
        listener = new CallServiceListener() {
            @Override
            public void onJoinCall(String id) {
                startRecording();
                for (UserModel user : room.getUserList()) {
                    String userId = user.getUserId();
                    if (!userId.equals(id)) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                callJavaScript("connect",userId);
                            }
                        });

                    }
                }
            }

            @Override
            public void onToogleMic() {
                if (!Info.isMute) {
                    startRecording();
                } else {
                    stopRecording();
                }

                userModel.setMute(Info.isMute);
                FirebaseUtils.updateUserData(room.getCode(), userModel, null);
                modifyNotification();
            }

            @Override
            public void onToogleDeafen() {
                if (Info.isDeafen) {
                    if (isRecording) {
                        stopRecording();
                    }
                } else {
                    if (!Info.isMute) {
                        startRecording();
                    }
                }

                userModel.setDeafen(Info.isDeafen);
                FirebaseUtils.updateUserData(room.getCode(), userModel, null);
                modifyNotification();

            }

            @Override
            public void onDisconnect() {
                if (webView != null) {
                    webView.loadUrl("");
                    Toast.makeText(CallService.this, "Disconnected!", Toast.LENGTH_SHORT).show();
                }

                if (notificationManager != null) {
                    notificationManager.cancelAll();
                }
                stopSelf();
                if (PlayerActivity.listener == null) {
                    return;
                }
                PlayerActivity.listener.onDisconnect();
            }


            @Override
            public void sendMessage(MessageModel messageModel) {
                //String str = ObjectUtil.objectToStr(messageModel);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callJavaScriptBytes("sendMessage",messageModel);
                    }
                });
            }

            @Override
            public void onSendSeekInfo(long positionMs) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callJavaScript("sendSeekInfo", positionMs);
                    }
                });
            }

            @Override
            public void onSendPlayPauseInfo(boolean isPlaying) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callJavaScript("sendPlayPauseInfo", isPlaying);
                    }
                });
            }

            @Override
            public void onSendPlaybackState(String id, boolean isPlaying, long currentPosition) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callJavaScript("sendPlaybackState", id, isPlaying, currentPosition);
                    }
                });
            }

            @Override
            public void onSendPlaybackStateRequest() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callJavaScript("sendPlaybackStateRequest");
                    }
                });
            }

            @Override
            public void onActivityStopInfo() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callJavaScript("sendActivityStopInfo", userModel.getName(), System.currentTimeMillis());
                    }
                });
            }

            @Override
            public void onSendJoinedPartyAgain() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callJavaScript("sendJoinedPartyAgain", userModel.getName(), System.currentTimeMillis());
                    }
                });
            }
        };
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        webView = new WebView(this);
        webView.getSettings().setJavaScriptEnabled(true);
//        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onPermissionRequest(PermissionRequest request) {
                request.grant(request.getResources());
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                boolean x = !url.equals("file:///android_asset/call.html");
                if (x) {
                    return;
                }
                callJavaScript("init", room.getUser().getUserId());
            }

        });
        javascriptInterface = new JavaScriptInterface(CallService.this);
        webView.addJavascriptInterface(javascriptInterface, "Android");

        WebSettings webSettings = webView.getSettings();
        webSettings.setDomStorageEnabled(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false);

        String path = "file:android_asset/call.html";
        webView.loadUrl(path);


    }

//    private String objToString(Object... items) {
//        StringJoiner joiner = new StringJoiner(",");
//        for (Object item : items) {
//            if (item == null) {
//                item = "null";
//            }
//            joiner.add(item.toString());
//        }
//        return joiner.toString();
//    }

//    private String stringToString(Object... items) {
//        StringBuilder result = new StringBuilder();
//        for (int i = 0; i < items.length; i++) {
//            Object item = items[i];
//            if (item instanceof String) {
//                result.append("\"").append(item).append("\"");
////                result.append(item);
//            } else {
//                result.append(item);
//            }
//            // Separate items with a comma if it's not the last item
//            if (i < items.length - 1) {
//                result.append(",");
//            }
//        }
//        return result.toString();
//    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void callJavaScript(String func,Object... args) {
        StringBuilder argString = new StringBuilder();
        for (Object arg : args) {
            if (arg instanceof String) {
                argString.append("'").append(arg).append("'");
            } else {
                argString.append(arg.toString());
            }
            argString.append(",");
        }
        if (argString.length() > 0) {
            argString.deleteCharAt(argString.length() - 1); // Remove the trailing comma
        }
        final String javascriptCommand = String.format("javascript:%s(%s)", func, argString.toString());
        webView.loadUrl(javascriptCommand);
    }

    public void callJavaScriptBytes(String func,Object obj) {
        String javascriptCommand = String.format("javascript:%s(%s)", func, ObjectUtil.objectToStr(obj));
        webView.loadUrl(javascriptCommand);
    }


    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        if (notificationManager != null) {
            notificationManager.cancelAll();
        }
    }

    @SuppressLint("RestrictedApi")
    private void modifyNotification() {
        String muteLabel = Info.isMute ? "Unmute" : "Mute";
        String deafenLabel = Info.isDeafen ? "Undeafen" : "Deafen";
        builder.mActions.clear(); // Clear existing actions

        // Add updated actions
        builder.addAction(R.drawable.call_end, "Disconnect", hangupPendingIntent)
                .addAction(R.drawable.mic_on, muteLabel, mutePendingIntent)
                .addAction(R.drawable.deafen_on, deafenLabel, deafenPendingIntent);
//        builder.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
//                .setVibrate(new long[]{0L});
        // Update the notification
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, builder.build());
        }
    }

    private void createNotification() {

        room.setRoomType(RoomType.PENDING);

        Intent callIntent = new Intent(this, PlayerActivity.class);
        callIntent.putExtra(getString(R.string.room), room);
        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, callIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        // Create an explicit intent for the activity that handles the button actions
        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.setAction("com.github.jaykkumar01.watchparty.receivers.ACTION_MUTE_HANGUP");
        intent.putExtra("requestCode", REQUEST_CODE_MUTE);

        mutePendingIntent = PendingIntent.getBroadcast(this, REQUEST_CODE_MUTE, intent, PendingIntent.FLAG_UPDATE_CURRENT |  PendingIntent.FLAG_IMMUTABLE);
        intent.putExtra("requestCode", REQUEST_CODE_HANGUP);
        hangupPendingIntent = PendingIntent.getBroadcast(this, REQUEST_CODE_HANGUP, intent, PendingIntent.FLAG_UPDATE_CURRENT |  PendingIntent.FLAG_IMMUTABLE);
        intent.putExtra("requestCode",REQUEST_CODE_DEAFEN);
        deafenPendingIntent = PendingIntent.getBroadcast(this, REQUEST_CODE_DEAFEN, intent, PendingIntent.FLAG_UPDATE_CURRENT |  PendingIntent.FLAG_IMMUTABLE);

        String muteLabel = Info.isMute? "Unmute" : "Mute";
        String deafenLabel = Info.isDeafen? "Undeafen" : "Deafen";
        // Create the notification
        builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.call)
                .setContentTitle("Voice Connected")
                .setContentText("Tap to manage the call")
                .setPriority(Notification.PRIORITY_DEFAULT)
                .addAction(R.drawable.call_end, "Disconnect", hangupPendingIntent)
                .addAction(R.drawable.mic_on, muteLabel, mutePendingIntent)
                .addAction(R.drawable.deafen_on, deafenLabel, deafenPendingIntent)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setOngoing(true);
        builder.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                .setVibrate(new long[]{0L});

        //notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager = getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Create the notification channel for Android Oreo and above
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
                channel.setDescription("channelDescription");
                channel.setVibrationPattern(new long[]{0L});
                channel.enableVibration(true);

                notificationManager.createNotificationChannel(channel);
            }
            startForeground(NOTIFICATION_ID, builder.build());
            notificationManager.notify(NOTIFICATION_ID, builder.build());



        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        listener = null;
        FirebaseUtils.removeUserData(room.getCode(),room.getUser());
    }
}
