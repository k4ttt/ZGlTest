package com.example.zglsurface;

import android.content.res.AssetManager;

public class GlNativeHelper {
    public static String tag = "ZGlHelperJava";

    public static void log(String str, Object... arg) {
        System.out.println(tag + ":" +
                String.format(str, arg));
    }

    public static native long renderOnSurCreated(AssetManager assetManager, int mRadius, int mStep);

    public static native void renderOnSurChanged(long obj, int width, int height);

    public static native void deliverTouchEvent(long obj, float deltX, float deltY, float x, float y);

    public static native void renderOnDrawFrame(long obj);

    public static native void setTextureId(long obj, int textId);

    public static native void deleteNativeObj(long mObj);

    static {
        System.loadLibrary("zGlHelper");
    }
}
