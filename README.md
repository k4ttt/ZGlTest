## 安卓平台基于opengl es的模糊效果

### 添加依赖

模块gradle

```
dependencies {
...
    implementation 'com.github.k4ttt:ZGlTest:1.0.0'
}
```

项目gradle

```
allprojects {
    repositories {
...
        maven { url "https://jitpack.io" }
    }
}
```

### layout文件中使用

| 属性           | 说明     |
| -------------- | -------- |
| app:zgl_step   | 模糊步长 |
| app:zgl_radius | 模糊半径 |
| app:zgl_src    | 模糊图片 |

```
   <com.example.zglsurface.ZGlSurfaceView
        android:id="@+id/zglsv"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:zgl_radius="20"
        app:zgl_src="@drawable/timg"
        app:zgl_step="5" />
```

### java文件中使用

```
 zglsv.setSrcBitmap(BitmapFactory.decodeResource(
                getResources(),
                R.drawable.timg
        ));
```

### 效果示例

![](https://raw.githubusercontent.com/k4ttt/ZGlTest/release/example.png)

