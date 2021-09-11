package com.hhd.logsloth;

import com.google.gson.GsonBuilder;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class LsUtil {
    public static String toJsonStr(Object obj) {
        String res = new GsonBuilder()
                .setPrettyPrinting()
                .create()
                .toJson(obj);

        return res;
    }

    public static String toStr_ByteBuffer(ByteBuffer buf) {
        String res = String.format("%d:%d:%d:%d", buf.position(), buf.limit(), buf.capacity(), buf.remaining());
        return res;
    }

    public static String toStr_FloatBuffer(FloatBuffer buf) {
        String res = String.format("%d:%d:%d:%d", buf.position(), buf.limit(), buf.capacity(), buf.remaining());
        return res;
    }
}
