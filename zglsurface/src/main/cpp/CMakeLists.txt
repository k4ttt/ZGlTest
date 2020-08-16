cmake_minimum_required(VERSION 3.4.1)
add_library(
        zGlHelper
        SHARED
        ZGlHelper.cpp
#        esUtil.c
)

target_link_libraries(
        zGlHelper
        GLESv3
        GLESv2
        GLESv1_CM
        android
        EGL
        log
        m
)