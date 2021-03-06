/*
	Copyright (c) 2012-2016 EasyDarwin.ORG.  All rights reserved.
	Github: https://github.com/EasyDarwin
	WEChat: EasyDarwin
	Website: http://www.easydarwin.org
*/

package org.easydarwin.easyipcamera.camera;

import android.util.Log;
import android.util.SparseArray;

/**
 * Created by Kim on 8/8/2016.
 */

public class EasyIPCamera {
    private static int sKey;
    private int mCtx;
    private static final SparseArray<IPCameraCallBack> sCallbacks = new SparseArray<>();

    private static final String TAG = "EasyIPCamera";

    static{
        System.loadLibrary("EasyIPCamera");
    }

    public static class ChannelState {
        public static final int EASY_IPCAMERA_STATE_ERROR                  =   -1;       //内部错误
        public static final int EASY_IPCAMERA_STATE_REQUEST_MEDIA_INFO   =   1;       //新连接,请求media info
        public static final int EASY_IPCAMERA_STATE_REQUEST_PLAY_STREAM  =   2;       //开始发送流
        public static final int EASY_IPCAMERA_STATE_REQUEST_STOP_STREAM  =   3;       //停止流
    }

    public static class FrameFlag{
        public static final int EASY_SDK_VIDEO_FRAME_FLAG = 0x01;/* 视频帧标志 */
        public static final int EASY_SDK_AUDIO_FRAME_FLAG = 0x02;/* 音频帧标志 */
    }

    public static class AuthType{
        public static final int AUTHENTICATION_TYPE_BASIC		= 0;
        public static final int AUTHENTICATION_TYPE_DIGEST      = 1;
    }

    /* 视频编码 */
    public static class VideoCodec{
        public static final int EASY_SDK_VIDEO_CODEC_H264 = 0x1C;		/* H264  */
        public static final int EASY_SDK_VIDEO_CODEC_H265 = 0x48323635;		/* H265  */
        public static final int EASY_SDK_VIDEO_CODEC_MJPEG = 0x08;		/* MJPEG  */
        public static final int EASY_SDK_VIDEO_CODEC_MPEG4 = 0x0D;		/* MPEG4  */
    }

    /* 音频编码 */
    public static class AudioCodec{
        public static final int EASY_SDK_AUDIO_CODEC_AAC = 0x15002;		/* AAC  */
        public static final int EASY_SDK_AUDIO_CODEC_G711U = 0x10006;		/* G711 ulaw  */
        public static final int EASY_SDK_AUDIO_CODEC_G711A = 0x10007;		/* G711 alaw  */
        public static final int EASY_SDK_AUDIO_CODEC_G726 = 0x1100B;		/* G726  */
    }

    int registerCallback(IPCameraCallBack cb) {
        synchronized (sCallbacks) {
            sCallbacks.put(++sKey, cb);
            return sKey;
        }
    }

    void unrigisterCallback(IPCameraCallBack cb) {
        synchronized (sCallbacks) {
            int idx = sCallbacks.indexOfValue(cb);
            if (idx != -1) {
                sCallbacks.removeAt(idx);
            }
        }
    }

    /* 回调函数定义 userptr表示用户自定义数据 */
    public static void onIPCameraCallBack(int channelId, int channelState, byte[] mediaInfo, int userPtr){
        Log.d(TAG, "kim onIPCameraCallBack channelId="+channelId+", channelState="+channelState);
        //TODO::

        synchronized (sCallbacks) {
            final IPCameraCallBack callBack = sCallbacks.get(channelId);
            if (callBack != null) {
                callBack.onIPCameraCallBack(channelId, channelState, mediaInfo, userPtr);
            }
        }

        return;
    }

    /* 启动 Rtsp Server */
	/*设置监听端口, 回调函数及自定义数据 */
    public native int startup(int listenport, int authType, String realm, String username, String password, int userptr,int channelid, byte[] channelinfo);

    /* 终止 Rtsp Server */
    public native int shutdown();

    /* frame:  具体发送的帧数据 */
    public native int pushFrame(int channelId, int avFrameFlag, long timestamp, byte[] pBuffer);

    public native int resetChannel(int channelId);

    public interface IPCameraCallBack {
        void onIPCameraCallBack(int channelId, int channelState, byte[] mediaInfo, int userPtr);
    }
}