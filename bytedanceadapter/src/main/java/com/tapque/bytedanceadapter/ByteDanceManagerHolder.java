package com.tapque.bytedanceadapter;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.bytedance.sdk.openadsdk.TTAdConfig;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdSdk;

public class ByteDanceManagerHolder {


    private   static String ttappid;
    private static boolean sInit;
    public static TTAdManager get() {
        if (!sInit) {
            throw new RuntimeException("TTAdSdk is not init, please check.");
        }
        return TTAdSdk.getAdManager();
    }
    public static void init(Activity activity ,String appid) {
        if (!sInit) {
            ttappid=appid;
            doInit(activity);
            sInit = true;
        }
    }
    private static void doInit(Activity activity) {
        TTAdSdk.init(activity, buildConfig(activity));
    }
    private static TTAdConfig buildConfig(Activity activity) {
        return new TTAdConfig.Builder()
                .appId(ttappid)
                .useTextureView(false) //使用TextureView控件播放视频,默认为SurfaceView,当有SurfaceView冲突的场景，可以使用TextureView
                .appName("测试项目")
                .titleBarTheme(TTAdConstant.TITLE_BAR_THEME_DARK)
                .allowShowNotify(true) //是否允许sdk展示通知栏提示
                .allowShowPageWhenScreenLock(true) //是否在锁屏场景支持展示广告落地页
                .debug(false) //测试阶段打开，可以通过日志排查问题，上线时去除该调用
                .directDownloadNetworkType(TTAdConstant.NETWORK_STATE_WIFI, TTAdConstant.NETWORK_STATE_3G,TTAdConstant.NETWORK_STATE_MOBILE,TTAdConstant.NETWORK_STATE_2G,TTAdConstant.NETWORK_STATE_4G) //允许直接下载的网络状态集合
                .supportMultiProcess(false)
                .build();
    }

}
