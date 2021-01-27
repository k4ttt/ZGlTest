//
// Created by tt on 2021/1/7.
//
#include <pthread.h>
#include <libavformat/avformat.h>
#include <libavfilter/buffersrc.h>
#include <libswscale/swscale.h>
#include "z_video_helper.h"

ANativeWindow *aNativeWindow;
int width;
int height;

static int open_codec_context(int *stream_idx,
                              AVCodecContext **dec_ctx, AVFormatContext *fmt_ctx,
                              enum AVMediaType type) {
    int ret, stream_index;
    AVStream *st;
    AVCodec *dec = NULL;
    AVDictionary *opts = NULL;
    ret = av_find_best_stream(fmt_ctx, type, -1, -1, NULL, 0);
    if (ret < 0) {
        zlog("Could not find %s stream'\n",
             av_get_media_type_string(type));
        return ret;
    } else {
        stream_index = ret;
        st = fmt_ctx->streams[stream_index];

        /* find decoder for the stream */
        dec = avcodec_find_decoder(st->codecpar->codec_id);
        if (!dec) {
            zlog("Failed to find %s codec\n",
                 av_get_media_type_string(type));
            return AVERROR(EINVAL);
        }

        /* Allocate a codec context for the decoder */
        *dec_ctx = avcodec_alloc_context3(dec);
        if (!*dec_ctx) {
            zlog("Failed to allocate the %s codec context\n",
                 av_get_media_type_string(type));
            return AVERROR(ENOMEM);
        }

        /* Copy codec parameters from input stream to output codec context */
        if ((ret = avcodec_parameters_to_context(*dec_ctx, st->codecpar)) < 0) {
            zlog("Failed to copy %s codec parameters to decoder context\n",
                 av_get_media_type_string(type));
            return ret;
        }

        /* Init the decoders, with or without reference counting */
        av_dict_set(&opts, "refcounted_frames", "0", 0);
        if ((ret = avcodec_open2(*dec_ctx, dec, &opts)) < 0) {
            zlog("Failed to open %s codec\n",
                 av_get_media_type_string(type));
            return ret;
        }
        *stream_idx = stream_index;
    }

    return 0;
}

void *run_ffmpeg_work(void *arg) {
    zlog("run_ffmpeg_work %d", *(int *) arg);
    av_register_all();

    AVFormatContext *fmt_ctx = NULL;
    const char *src_filename = "/sdcard/testVideo/womenkill";

    if (avformat_open_input(&fmt_ctx, src_filename, NULL, NULL) < 0) {
        zlog("Could not open source file %s\n", src_filename);
        exit(1);
    }

    if (avformat_find_stream_info(fmt_ctx, NULL) < 0) {
        zlog("Could not find stream information\n");
        exit(1);
    }

    int video_stream_idx = 0;
    AVCodecContext *video_dec_ctx = NULL;
    zlog("bit rate %lld", fmt_ctx->bit_rate);
    zlog("duration %lld", fmt_ctx->duration);

    if (open_codec_context(&video_stream_idx, &video_dec_ctx, fmt_ctx, AVMEDIA_TYPE_VIDEO) >= 0) {
        zlog("video_stream_idx %d", video_stream_idx);

        AVPacket packet;
        AVFrame *frame = av_frame_alloc();
        ANativeWindow_Buffer aNativeWindowBuffer;

        int ret;
        while (1) {
            if ((ret = av_read_frame(fmt_ctx, &packet)) < 0) {
                zlog("read frame %d", ret);
                break;
            }
            zlog("packet stream index %d", packet.stream_index);

            if (packet.stream_index == video_stream_idx) {
                zlog("packet stream pts %lld", packet.pts);

                ret = avcodec_send_packet(video_dec_ctx, &packet);
                if (ret < 0) {
                    zlog("Error while sending a packet to the decoder %d", ret);
                    continue;
                }

                while (ret >= 0) {
                    ret = avcodec_receive_frame(video_dec_ctx, frame);
                    zlog("receive frame %d", ret);
                    if (ret == AVERROR(EAGAIN) || ret == AVERROR_EOF) {
                        zlog("receive frame end");
                        break;
                    } else if (ret < 0) {
                        zlog("Error while receiving a frame from the decoder\n");
                        break;
                    }

                    if (ret >= 0) {
                        zlog("frame %d", frame->linesize[0]);

                        int ret = ANativeWindow_lock(aNativeWindow, &aNativeWindowBuffer, NULL);
                        zlog("lock ret %d", ret);
//                        uint32_t *line = (uint32_t *) aNativeWindowBuffer.bits;
//                        for (int y = 0; y < aNativeWindowBuffer.height; y++) {
//                            int lineValue = random();
//                            for (int x = 0; x < aNativeWindowBuffer.width; x++) {
//                                line[x] = lineValue;
//                            }
//                            line = line + aNativeWindowBuffer.stride;
//                        }

                        for (int h = 0; h < aNativeWindowBuffer.height; h++) {
                            memcpy(aNativeWindowBuffer.bits +
                                   h * aNativeWindowBuffer.stride * 4,
                                   frame->data[0] + h * frame->linesize[0],
                                   aNativeWindowBuffer.stride * 4);
                        }

                        av_frame_unref(frame);
                        ANativeWindow_unlockAndPost(aNativeWindow);
                        zlog("unlock end");
//                        ANativeWindow_release(aNativeWindow);
//                        zlog("release end");

                    }
                }
            }
            av_packet_unref(&packet);
        }
    }

    /* dump input information to stderr */
    av_dump_format(fmt_ctx, 0, src_filename, 0);

    avcodec_free_context(&video_dec_ctx);
    avformat_close_input(&fmt_ctx);

    zlog("test:1");
    //还可以这样进行字符串合并！
    zlog("asa""ss""aaaaa");
    int a = 3;
    PSQR(a);
}

JNI_FUNC(int, start) {
    pthread_t run;
    int arg = 1;
    int ret = pthread_create(&run, NULL, run_ffmpeg_work, &arg);
    zlog("thread ret %d", ret);
    return 1;
}

JNI_FUNC(int, processTextureView, jobject textureview) {
    zlog("get tex window start %s", textureview);
    jclass tex_class = (*env)->GetObjectClass(env, textureview);
    if (!tex_class) {
        zlog("can not get jclass");
    }
    jfieldID fid = (*env)->GetFieldID(env, tex_class, "mNativeWindow", "J");
    if (!fid) {
        zlog("can not get jfieldID");
    }
    jlong window = (*env)->GetLongField(env, textureview, fid);
    zlog("window %lld", window);
    aNativeWindow = window;

    height = ANativeWindow_getHeight(aNativeWindow);
    width = ANativeWindow_getWidth(aNativeWindow);
    int format = ANativeWindow_getFormat(aNativeWindow);
    zlog("window %d %d %d", height, width, format);
//    ANativeWindow_Buffer aNativeWindowBuffer;
//    int ret = ANativeWindow_lock(aNativeWindow, &aNativeWindowBuffer, NULL);
//    zlog("lock ret %d", ret);
//    uint32_t *line = (uint32_t *) aNativeWindowBuffer.bits;
//    for (int y = 0; y < aNativeWindowBuffer.height; y++) {
//        int lineValue = random();
//        for (int x = 0; x < aNativeWindowBuffer.width; x++) {
////            for (int i = 1; i < 100; ++i) {
////                lineValue += i;
////            }
//            line[x] = lineValue;
//        }
//        line = line + aNativeWindowBuffer.stride;
//    }
//    ANativeWindow_unlockAndPost(aNativeWindow);
//    zlog("unlock end");
//    ANativeWindow_release(aNativeWindow);
//    zlog("release end");
    return 1;
}