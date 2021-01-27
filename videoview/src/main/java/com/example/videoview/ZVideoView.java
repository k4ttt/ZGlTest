package com.example.videoview;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.TextureView;

public class ZVideoView extends TextureView {
    public ZVideoView(Context context) {
        super(context);
        initView(null);
    }

    private void initView(AttributeSet o) {
        post(new Runnable() {
            @Override
            public void run() {
                setSurfaceTextureListener(new SurfaceTextureListener() {
                    @Override
                    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                        System.out.println("z_video " + System.currentTimeMillis());
                        ZVideoHelper.processTextureView(ZVideoView.this);
                        ZVideoHelper.start();
                    }

                    @Override
                    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

                    }

                    @Override
                    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                        return false;
                    }

                    @Override
                    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

                    }
                });
            }
        });
    }

    public ZVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
    }

    public ZVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs);
    }


}
