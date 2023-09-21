package com.github.jaykkumar01.watchparty.services;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.github.jaykkumar01.watchparty.PlayerActivity;
import com.github.jaykkumar01.watchparty.R;
import com.github.jaykkumar01.watchparty.enums.RoomType;
import com.github.jaykkumar01.watchparty.interfaces.CallServiceListener;
import com.github.jaykkumar01.watchparty.interfaces.Data;
import com.github.jaykkumar01.watchparty.models.AgoraConfig;
import com.github.jaykkumar01.watchparty.models.Room;
import com.github.jaykkumar01.watchparty.models.UserModel;
import com.github.jaykkumar01.watchparty.receivers.NotificationReceiver;
import com.github.jaykkumar01.watchparty.utils.FirebaseUtils;

import java.util.List;

public class CallService extends Service implements CallServiceListener, Data {

    private WebView webView;
    private String code = null;
    private Room room;
    private AgoraConfig agoraConfig;
    private UserModel userModel;
    private NotificationManager notificationManager;

    public static CallServiceListener listener;
    public static boolean isMute, isDeafen;
    private int max = 0;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        listener = this;
        if (intent != null) {
            room = (Room) intent.getSerializableExtra(getString(R.string.room));
            agoraConfig = room.getAgoraConfig();
            userModel = room.getUser();
            code = room.getCode();
            if (room.getRoomType() == RoomType.CREATED){
                createNotification(isMute,isDeafen);
            }

        }
        setupWebView();


        return START_STICKY;
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        webView = new WebView(this);
        webView.getSettings().setJavaScriptEnabled(true);
//        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onPermissionRequest(PermissionRequest request) {
                request.grant(request.getResources());
            }
        });

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                boolean x = !url.equals("file:///android_asset/call.html");
                if(x){
                    return;
                }
//                view.loadUrl("javascript:test()");
                callJavaScript("init(\""
                        + agoraConfig.getId() +"\",\""
                        + agoraConfig.getToken()+"\",\""
                        +agoraConfig.getChannel()+"\")");
//                callJavaScript("javascript:test()");
                //Toast.makeText(CallService.this, "Started", Toast.LENGTH_SHORT).show();
            }

        });
        webView.addJavascriptInterface(new JavaScriptInterface(), "Android");

        WebSettings webSettings = webView.getSettings();
        webSettings.setDomStorageEnabled(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false);

        String path = "file:android_asset/call.html";
        webView.loadUrl(path);


    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public void callJavaScript(String func) {
        webView.evaluateJavascript(func, null);
    }


    @Override
    public void onJoinCall(boolean call, List<UserModel> userList,long joinTime) {
        if(call){
            callAllUsers(userList);
            userModel.setJoinTime(joinTime);
            //FirebaseUtils.updateUserData(code, userModel);
            createNotification(isMute,isDeafen);
        }
        else{
            onDisconnect();
        }

    }

    @Override
    public void onToogleMic() {
        isMute = !isMute;
        userModel.setMute(isMute);
        //FirebaseUtils.updateUserData(code, userModel);
        callJavaScript("javascript:toggleAudio(\""+!isMute+"\")");
        createNotification(isMute,isDeafen);
    }

    @Override
    public void onDisconnect() {
        if (webView != null) {
            webView.loadUrl("");
            callJavaScript("javascript:endCall()");
            FirebaseUtils.removeUserData(code, userModel);
            Toast.makeText(this, "Disconnected!", Toast.LENGTH_SHORT).show();
        }
        if (notificationManager != null) {
            notificationManager.cancelAll();
        }
        stopSelf();
    }

    @Override
    public void onToogleDeafen() {
        isDeafen = !isDeafen;
        userModel.setDeafen(isDeafen);
        //FirebaseUtils.updateUserData(code, userModel);
        callJavaScript("javascript:toggleAudio(\""+!isDeafen+"\")");
        callJavaScript("javascript:muteAllAudioElements("+isDeafen+")");
        createNotification(isMute,isDeafen);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        if (notificationManager != null) {
            notificationManager.cancelAll();
        }
    }

    private void callAllUsers(List<UserModel> userList) {
        StringBuilder stringBuilder = new StringBuilder("[");
        for (int i = 0; i < userList.size(); i++) {
            stringBuilder.append("'").append(userList.get(i).getUserId()).append(i == userList.size() - 1 ? "']" : "',");
        }
        callJavaScript("javascript:startCall(" + stringBuilder.toString() + ");");
    }

    private void createNotification(boolean isMute, boolean isDeafen) {


        Intent callIntent = new Intent(this, PlayerActivity.class);

        callIntent.putExtra("userModel", userModel);
        callIntent.putExtra("code", code);
        callIntent.putExtra("type","pendingIntent");
        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, callIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        // Create an explicit intent for the activity that handles the button actions
        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.setAction("com.github.jaykkumar01.watchparty.receivers.ACTION_MUTE_HANGUP");
        intent.putExtra("requestCode", REQUEST_CODE_MUTE);

        PendingIntent mutePendingIntent = PendingIntent.getBroadcast(this, REQUEST_CODE_MUTE, intent, PendingIntent.FLAG_UPDATE_CURRENT |  PendingIntent.FLAG_IMMUTABLE);
        intent.putExtra("requestCode", REQUEST_CODE_HANGUP);
        PendingIntent hangupPendingIntent = PendingIntent.getBroadcast(this, REQUEST_CODE_HANGUP, intent, PendingIntent.FLAG_UPDATE_CURRENT |  PendingIntent.FLAG_IMMUTABLE);
        intent.putExtra("requestCode",REQUEST_CODE_DEAFEN);
        PendingIntent deafenPendingIntent = PendingIntent.getBroadcast(this, REQUEST_CODE_DEAFEN, intent, PendingIntent.FLAG_UPDATE_CURRENT |  PendingIntent.FLAG_IMMUTABLE);

        String muteLabel = isMute? "Unmute" : "Mute";
        String deafenLabel = isDeafen? "Undeafen" : "Deafen";
        // Create the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.call)
                .setContentTitle("Voice Connected")
                .setContentText("Tap to manage the call")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .addAction(R.drawable.call_end, "Disconnect", hangupPendingIntent)
                .addAction(R.drawable.mic_on, muteLabel, mutePendingIntent)
                .addAction(R.drawable.mic_on, deafenLabel, deafenPendingIntent)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setOngoing(true);

        //notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager = getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Create the notification channel for Android Oreo and above
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
                channel.setDescription("channelDescription");


                notificationManager.createNotificationChannel(channel);
            }
            startForeground(NOTIFICATION_ID, builder.build());
            notificationManager.notify(NOTIFICATION_ID, builder.build());



        }
    }
    public class JavaScriptInterface {
        @JavascriptInterface
        public void onCallback(String message) {
            Toast.makeText(CallService.this, message, Toast.LENGTH_SHORT).show();
//            handler.post(runnable);
            // Handle the callback in the Java code
            // You can perform any necessary actions here
        }

        @JavascriptInterface
        public void onPeerConnected(){
            //Toast.makeText(CallService.this, "Peer Connected", Toast.LENGTH_SHORT).show();
        }
        @JavascriptInterface
        public void onStreamStarted(){
//            Toast.makeText(CallService.this, "Stream Connected", Toast.LENGTH_SHORT).show();
//            AudioFocusManager audioFocusManager = new AudioFocusManager(CallService.this);
//            audioFocusManager.requestAudioFocus();

        }
        @JavascriptInterface
        public void onPrintIntensity(String otherUserId,int averageIntensity){

            if (max<averageIntensity){
                max = averageIntensity;
            }

            Log.d("intensity",otherUserId+": "+averageIntensity);
            //Toast.makeText(CallService.this, "Peer Connected", Toast.LENGTH_SHORT).show();
        }
    }
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Log.d("intensityMax",max+" :max");
            max = 0;
            handler.postDelayed(this,5000);
        }
    };

}
