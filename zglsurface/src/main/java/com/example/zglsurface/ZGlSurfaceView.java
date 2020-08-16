package com.example.zglsurface;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class ZGlSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer {
    /**
     * native obj 对象
     */
    long mObj;
    private boolean hasDraw = false;
    private int mRadius;
    private int mStep;
    private int mSrc;
    private Bitmap mBitmap;
    private int mTargetTextId;

    public Bitmap getBitmapFromFile() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        return BitmapFactory.decodeResource(getResources()
                , mSrc, options);
    }

    public int genTextureIdAndFillData() {
        return genTextureIdAndFillData(mBitmap);
    }

    public int genTextureIdAndFillData(Bitmap bitmap) {
        if (bitmap == null) {
            return 0;
        }
        generateTargetTex();

        refreshTexData(bitmap);

        return mTargetTextId;
    }

    private void refreshTexData(Bitmap bitmap) {
        GlNativeHelper.log("refreshTexData:" + bitmap);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTargetTextId);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        /**
         * 第一个参数！！
         * GLES20.GL_TEXTURE_2D
         */
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        bitmap.recycle();
    }

    private void generateTargetTex() {
        if (mTargetTextId == 0) {
            int[] texture = new int[1];
            GLES20.glGenTextures(1, texture, 0);
            mTargetTextId = texture[0];
            GlNativeHelper.log("set texture id:" + mTargetTextId);
        }
    }

    public ZGlSurfaceView(Context context) {
        super(context);
        initView(null);
    }

    public ZGlSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
    }

    private void initView(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ZGlSurfaceView);
            if (typedArray != null) {
                mRadius = typedArray.getInteger(R.styleable.ZGlSurfaceView_zgl_radius, 10);
                mStep = typedArray.getInteger(R.styleable.ZGlSurfaceView_zgl_step, 1);
                mSrc = typedArray.getResourceId(R.styleable.ZGlSurfaceView_zgl_src, 0);
                mBitmap = getBitmapFromFile();
            }
        }

//        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        setEGLContextClientVersion(2);

        setEGLConfigChooser(new MyConfigChooser());

        setRenderer(this);


        /**
         * 不会一直调用draw，绘图时才会draw
         */
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GlNativeHelper.log("onSurfaceCreated");

        mObj = GlNativeHelper.renderOnSurCreated(getContext().getAssets(), mRadius, mStep);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GlNativeHelper.log("onSurfaceChanged");

        GlNativeHelper.log(
                "width:%d,height:%d", width, height
        );
        GlNativeHelper.renderOnSurChanged(mObj, width, height);
    }

    public void setSrcBitmap(Bitmap bitmap) {
        GlNativeHelper.log("setSrcBitmap:" + bitmap);
        mBitmap = bitmap;
        hasDraw = false;
        requestRender();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        GlNativeHelper.log("onDetachedFromWindow");
        GlNativeHelper.deleteNativeObj(mObj);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        GlNativeHelper.log("onAttachedToWindow");
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (mBitmap == null) {
            GlNativeHelper.log("do not draw");
            return;
        }
        GlNativeHelper.log("onDrawFrame:" + mBitmap + " " + mBitmap.isRecycled() + " " + hasDraw);

        if (!hasDraw) {
            hasDraw = true;
            genTextureIdAndFillData();
            GlNativeHelper.setTextureId(mObj, mTargetTextId);
        }

////        } else {
        GlNativeHelper.renderOnDrawFrame(mObj);
    }

    class MyConfigChooser implements EGLConfigChooser {
        @Override
        public EGLConfig chooseConfig(EGL10 egl,
                                      javax.microedition.khronos.egl.EGLDisplay display) {

            int attribs[] = {
                    EGL10.EGL_LEVEL, 0,
                    EGL10.EGL_RENDERABLE_TYPE, 4,  // EGL_OPENGL_ES2_BIT
                    EGL10.EGL_COLOR_BUFFER_TYPE, EGL10.EGL_RGB_BUFFER,
//                    EGL10.EGL_ALPHA_SIZE, 8,
                    EGL10.EGL_RED_SIZE, 8,
                    EGL10.EGL_GREEN_SIZE, 8,
                    EGL10.EGL_BLUE_SIZE, 8,
                    EGL10.EGL_DEPTH_SIZE, 16,
                    EGL10.EGL_SAMPLE_BUFFERS, 1,
                    EGL10.EGL_SAMPLES, 4,  // 修改MSAA的倍数，4就是4xMSAA，再往上开程序可能会崩
                    EGL10.EGL_NONE
            };
            EGLConfig[] configs = new EGLConfig[1];
            int[] configCounts = new int[1];
            egl.eglChooseConfig(display, attribs, configs, 1, configCounts);

            if (configCounts[0] == 0) {
                // Failed! Error handling.
                return null;
            } else {
                return configs[0];
            }
        }
    }

    float lastX;
    float lastY;

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//         GlNativeHelper.log(
//                        "getx:%f,gety:%f,rawx:%f,rawy:%f,action:%d",
//                        event.getX(),
//                        event.getY(),
//                        event.getRawX(),
//                        event.getRawY(),
//                        event.getAction()
//        );
//
//        if (lastX != 0 && lastY != 0) {
//            float deltaX = event.getX() - lastX;
//            float deltaY = event.getY() - lastY;
//             GlNativeHelper.log(
//                    String.format("deliverTouchEvent %f, %f", deltaX, deltaY));
//            GlNativeHelper.deliverTouchEvent(
//                    mObj,
//                    deltaX,
//                    deltaY,
//                    event.getX(),
//                    event.getY()
//            );
//            requestRender();
//        }
//
//        if (event.getAction() == MotionEvent.ACTION_UP) {
//            lastX = 0;
//            lastY = 0;
//        } else {
//            lastX = event.getX();
//            lastY = event.getY();
//        }
//        return true;
//    }


}
