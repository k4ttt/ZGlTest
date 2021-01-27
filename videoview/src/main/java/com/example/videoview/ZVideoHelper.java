package com.example.videoview;

import android.view.TextureView;

public class ZVideoHelper {
    static {
        System.loadLibrary("ZVideoHelper");
    }

    native public static int start();
    native public static int processTextureView(TextureView textureView);
}
