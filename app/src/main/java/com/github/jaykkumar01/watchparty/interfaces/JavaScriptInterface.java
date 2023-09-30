package com.github.jaykkumar01.watchparty.interfaces;

import android.content.Context;
import android.content.res.Resources;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.os.Build;
import android.os.Environment;
import android.telecom.Call;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.github.jaykkumar01.watchparty.models.AudioPlayerModel;
import com.github.jaykkumar01.watchparty.models.MessageModel;
import com.github.jaykkumar01.watchparty.models.UserModel;
import com.github.jaykkumar01.watchparty.services.CallService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JavaScriptInterface implements Data{
    Context context;

    HashMap<String, AudioPlayerModel> playerMap = new HashMap<>();

    ExecutorService executorService = Executors.newSingleThreadExecutor();

    public JavaScriptInterface(Context context) {
        this.context = context;
    }

    @JavascriptInterface
    public void onConnected(String id){
//        Toast.makeText(context, "myId: "+id, Toast.LENGTH_SHORT).show();
        CallService.listener.onJoinCall(id);
    }

    @JavascriptInterface
    public void sendToast(String toast){
        Toast.makeText(context, ""+toast, Toast.LENGTH_SHORT).show();
    }
    @JavascriptInterface
    public void onClose(String id){
//        PeerService.listener.onStop();
        Toast.makeText(context, "Closed: "+id, Toast.LENGTH_SHORT).show();
    }
    @JavascriptInterface
    public void showText(String txt){
//        MainActivity.listener.onRead(txt);
    }


    @JavascriptInterface
    public void send(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    private void saveBytesToFile(byte[] bytes, int read) {
        try {
            File externalDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File filePath = new File(externalDir, "testing/received_audio.pcm");
            File destPath = new File(externalDir, "testing/received_audio.wav");
            FileOutputStream fos = new FileOutputStream(filePath, true); // Use "true" to append to the existing file
            fos.write(bytes, 0, read);
            fos.close();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                PCM.rawToWave(filePath,destPath,SAMPLE_RATE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void sendMessage(String name,String txt){
        Toast.makeText(context, name+": "+txt, Toast.LENGTH_SHORT).show();
    }
    @JavascriptInterface
    public void play(String id,byte[] bytes, int read, long millis,String name, String message) {
        if (message != null){
            MessageModel messageModel = new MessageModel(id,message);
            messageModel.setName(name);
            messageModel.setTimeMillis(millis);
            CallService.listener.receiveMessage(messageModel);

//            Locale locale = Resources.getSystem().getConfiguration().getLocales().get(0);
//            SimpleDateFormat dateFormat = new SimpleDateFormat("dd LLL yyyy",locale);
//            String msgDate = dateFormat.format(messageModel.getTimeMillis());
//            Toast.makeText(context, millis+": "+msgDate, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!playerMap.containsKey(id)){
            AudioPlayerModel model = new AudioPlayerModel(id,millis);
            playerMap.put(id,model);
        }

        AudioPlayerModel audioPlayerModel = playerMap.get(id);
        if (audioPlayerModel == null){
            return;
        }


        executorService.execute(new Runnable() {
            @Override
            public void run() {

                long diff = System.currentTimeMillis() - millis - audioPlayerModel.getOffset();
                if (diff > 1500){
                    return;
                }

//                if (++delayCount <= 10){
//                    sumDelay += diff;
//
//                    int avg = (int) (sumDelay/delayCount) + 200;
//                    delay = avg/2;
//                }
//
//                int divider = (int) Math.max(1, diff / delay);
                int divider = 1;
                audioPlayerModel.getAudioTrack().write(bytes,0,read);
//                audioTrack.write(bytes,0,read/divider);
            }
        });
    }


    private class ByteData {
        byte[] bytes;
        int read;
        long millis;
        boolean played;

        public ByteData() {
        }

        public boolean isPlayed() {
            return played;
        }

        public void setPlayed(boolean played) {
            this.played = played;
        }

        public ByteData(byte[] bytes) {
            this.bytes = bytes;
        }

        public ByteData(byte[] bytes, int read, long millis) {
            this.bytes = bytes;
            this.read = read;
            this.millis = millis;
        }

        public byte[] getBytes() {
            return bytes;
        }

        public void setBytes(byte[] bytes) {
            this.bytes = bytes;
        }

        public int getRead() {
            return read;
        }

        public void setRead(int read) {
            this.read = read;
        }

        public long getMillis() {
            return millis;
        }

        public void setMillis(long millis) {
            this.millis = millis;
        }
    }
}
