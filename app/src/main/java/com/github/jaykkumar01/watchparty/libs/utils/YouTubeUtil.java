package com.github.jaykkumar01.watchparty.libs.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YouTubeUtil {

    public static String extractVideoId(String url) {
        String defaultVideoId = "b3-gdDnybIg";
        try {
            String pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*";
            Pattern compiledPattern = Pattern.compile(pattern);
            Matcher matcher = compiledPattern.matcher(url);
            if (matcher.find()) {
                return matcher.group();
            }
        } catch (Exception e) {
        }
        return defaultVideoId;
    }
}
