package com.github.jaykkumar01.watchparty.utils;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import androidx.media3.common.C;

import com.github.jaykkumar01.watchparty.PlayerActivity;
import com.github.jaykkumar01.watchparty.models.MessageModel;

import org.apache.commons.lang3.SerializationUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class ObjectUtil {


    private static boolean isfirst;
    public static int count;

    public static String preserveString(String input) {
        return Arrays.toString(input.getBytes(StandardCharsets.UTF_8));
    }
    public static String restoreString(byte[] bytes){
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static String preserveBytes(byte[] byteArray) {
        return Arrays.toString(byteArray);
//        try {
//            ByteArrayOutputStream byteStream = new ByteArrayOutputStream(byteArray.length);
//            try (GZIPOutputStream gzipStream = new GZIPOutputStream(byteStream)) {
//                gzipStream.write(byteArray);
//            }
//            return Arrays.toString(byteStream.toByteArray());
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
    }

    public static byte[] restoreBytes(byte[] preservedBytes) {
        try {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            GZIPInputStream gzipStream = new GZIPInputStream(new ByteArrayInputStream(preservedBytes));
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = gzipStream.read(buffer)) != -1) {
                byteStream.write(buffer, 0, bytesRead);
            }
            byte[] b = byteStream.toByteArray();

            if (count++ < 5){
                PlayerActivity.listener.onResult(preservedBytes.length+ " : "+b.length);
            }


            return b;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }


    private static byte[] objectToArray(Object obj){
        return SerializationUtils.serialize((Serializable) obj);
    }
    public static Object bytesToObj(byte[] bytes){
        return SerializationUtils.deserialize(bytes);
    }

    public static String objectToStr(Object obj) {
        return Arrays.toString(objectToArray(obj));
    }
}
