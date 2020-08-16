package com.zhilai.opengllearn;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.zglsurface.GlNativeHelper;
import com.example.zglsurface.ZGlSurfaceView;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.zglsv)
    ZGlSurfaceView zglsv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
//        zglsv.setSrcBitmap(BitmapFactory.decodeResource(
//                getResources(),
//                R.drawable.timg
//        ));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @OnClick(R.id.zglsv)
    public void onViewClicked() {
//        File picFileDir = new File("/sdcard/DCIM/Camera/");
//        GlNativeHelper.log("picFileDir:" + picFileDir);
//        File[] list = picFileDir.listFiles();
//        if (list != null) {
//            Bitmap bitmap = null;
//            while (bitmap == null) {
//                int fileIndex = (int) (Math.random() * list.length);
//                String picPath = list[fileIndex].getAbsolutePath();
//                GlNativeHelper.log("click file:" + picPath);
//                bitmap = BitmapFactory.decodeFile(picPath);
//            }
//            zglsv.setSrcBitmap(bitmap);
//        }
    }
}
