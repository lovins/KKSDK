package com.tapque.adscn;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.bytedance.sdk.openadsdk.TTAdConfig;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdSdk;

/**
 * 可以用一个单例来保存TTAdManager实例，在需要初始化sdk的时候调用
 */
public class TTAdManagerHolder {
    private static boolean sInit;
    public static TTAdManager get() {
        if (!sInit) {
            throw new RuntimeException("TTAdSdk is not init, please check.");
        }
        return TTAdSdk.getAdManager();
    }
    public static void init(Activity activity) {
        doInit(activity);
    }
    //step1:接入网盟广告sdk的初始化操作，详情见接入文档和穿山甲平台说明
    private static void doInit(Activity activity) {
        if (!sInit) {
            TTAdSdk.init(activity, buildConfig(activity));
            sInit = true;
        }
    }
    private static TTAdConfig buildConfig(Activity activity) {
        String appid=Utils.readIntValueFromManifest(activity,"tt_app_id")+"";
        Log.e("启动项", "appid: "+appid);
        if(TextUtils.isEmpty(appid)){
            Log.e("启动项", "广告初始化异常:请在清单文件中配置tt_app_id meta-data字段属性");
            return null;
        }
        return new TTAdConfig.Builder()
                .appId(appid)
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
