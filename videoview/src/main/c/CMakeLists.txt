cmake_minimum_required(VERSION 3.4.1)

set(DIR ../../../../lib)

include_directories(include)

#set(CMAKE_CXX_FLAGS  "${CMAKE_CXX_FLAGS} -L ${DIR}/${ANDROID_ABI}")

add_library(ffmpeg
        SHARED
        IMPORTED
        )

set_target_properties(
        ffmpeg
        PROPERTIES
        IMPORTED_LOCATION
        ${DIR}/${ANDROID_ABI}/libffmpeg.so)

add_library(
        ZVideoHelper
        SHARED
        z_video_helper.c
)

target_link_libraries(
        ZVideoHelper
        GLESv3
        GLESv2
        GLESv1_CM
        android
        ffmpeg
        EGL
        log
        m
)
