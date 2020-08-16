#version 100
//在顶点着色阶段，
//如果没有用户自定义的默认精度，
//那么 int 和 float 都默认为 highp 级别；
//而在片元着色阶段，如果没有用户自定义的默认精度，
//没有默认精度了，我们必须在每个变量前放置精度描述符
precision lowp float;
precision mediump int;
uniform sampler2D mTexture;
uniform sampler2D mKernalTex;
uniform vec4 mConfig;
//uniform float mKernal[1000];
varying vec2 mTexCord;
void main() {
    gl_FragColor = texture2D(mTexture, mTexCord);

    int radius = int(mConfig.x);
    int square = radius*2+1;
    float radiusF = float(radius);
    float stepX = mConfig.y;
    float stepY = mConfig.z;
    float isVertical = mConfig.w;

    float stepKernal = 1.0/float(square-1);

    vec4 rst=vec4(0.0);
    float num=0.0;

    float startX = mTexCord.x-stepX*radiusF;
    float startY = mTexCord.y-stepY*radiusF;
    float kernalX = 0.0;
    float kernalY = 0.0;

    //加权和过大或过小超出限制，除以一个ratio？
    const float ratio = 10.0;

    if (isVertical==0.0){
        //垂直采样
        for (int i=0;i<square;i++){
            float tempKernal = texture2D(mKernalTex, vec2(0.0, kernalY)).a/ratio;
            rst += texture2D(mTexture, vec2(mTexCord.x, startY)) * tempKernal;
            num += tempKernal;
            kernalY+=stepKernal;
            startY+=stepY;
        }
    } else {
        for (int i=0;i<square;i++){
            float tempKernal = texture2D(mKernalTex, vec2(kernalX, 0.0)).a/ratio;
            rst += texture2D(mTexture, vec2(startX, mTexCord.y)) * tempKernal;
            num += tempKernal;
            kernalX+=stepKernal;
            startX+=stepX;
        }
    }
    gl_FragColor = rst/num;
    //        gl_FragColor = vec4(mTexCord.x, mTexCord.y, 1.0, 1.0);
    //    gl_FragColor = vec4(0.5, 0.5, 1.0, 1.0);
}
