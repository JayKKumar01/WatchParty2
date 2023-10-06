package com.github.jaykkumar01.watchparty.utils;

import com.github.jaykkumar01.watchparty.models.MessageModel;

import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.Arrays;

public class ObjectUtil {


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
