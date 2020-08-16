#version 100
attribute vec2 mPosAttr;
attribute vec2 mTexCordAttr;
varying vec2 mTexCord;

void main() {
    mTexCord = mTexCordAttr;
    gl_Position = vec4(mPosAttr, 0, 1.0);
}