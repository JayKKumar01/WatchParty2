package com.github.jaykkumar01.watchparty.libs.utils;

import android.content.Context;

import com.github.jaykkumar01.watchparty.libs.models.YouTubeApiInfo;
import com.github.jaykkumar01.watchparty.libs.models.YouTubeData;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class YouTubeAPI implements Runnable {
    Context context;
    Listener listener;
    String link;

    public YouTubeAPI(Context context,String link) {
        this.context = context;
        this.link = link;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public void run() {
        String apiUrl = "https://www.youtube.com/oembed?url=" + link + "&format=json";
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(500);
            connection.setRequestMethod("GET");
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();

                connection.disconnect();
                YouTubeApiInfo videoInfo = null;

                try {
                    Gson gson = new Gson();
                    videoInfo = gson.fromJson(content.toString(), YouTubeApiInfo.class);
                }catch (JsonSyntaxException e) {
                    throw new RuntimeException(e);
                }

                if (videoInfo == null || videoInfo.getTitle() == null){
                    listener.onComplete(false,null);
                    return;
                }
                YouTubeData youTubeData = new YouTubeData(videoInfo.getTitle());
                youTubeData.setLink(link);
                listener.onComplete(true,youTubeData);
            } else {
                listener.onComplete(false,null);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public interface Listener{
        void onComplete(boolean success, YouTubeData youTubeData);
    }

}
