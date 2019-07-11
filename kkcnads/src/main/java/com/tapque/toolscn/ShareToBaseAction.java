package com.tapque.toolscn;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.FileProvider;

import java.io.File;
import java.util.List;

@SuppressLint("InflateParams")
public class ShareToBaseAction {

    /**
     * Wrap IAppInfo list from ResolveInfo list
     * 获取分享app的集合
     *
     * @param ctx
     * @return
     */
    public List<IAppInfo> getShareAppList(Context ctx, List<IAppInfo> shareAppInfos, String type) {
        PackageManager packageManager = ctx.getPackageManager();

        List<ResolveInfo> resolveInfos = getShareApps(ctx, type);
        if (null == resolveInfos) {
            return null;
        } else {
            for (ResolveInfo resolveInfo : resolveInfos) {
                IAppInfo appInfo = new IAppInfo();
                appInfo.setAppPkgName(resolveInfo.activityInfo.packageName);
                appInfo.setAppLauncherClassName(resolveInfo.activityInfo.name);
                appInfo.setAppName(resolveInfo.loadLabel(packageManager).toString());
                appInfo.setAppIcon(resolveInfo.loadIcon(packageManager));
                shareAppInfos.add(appInfo);
            }
        }
        return shareAppInfos;
    }

    /**
     * Get installed APP list by type
     *  ApplictionInfo与ResolveInfo的比较：前者能够得到：Icon、Label、meta-date、description 后者只能得到：Icon、Label
     *  而IAppInfo是自定义的：appPkgName、appLauncherClassName、appName、appIcon
     * @param context
     * @param type
     * @return
     */
    /**
     * 与上面的不同
     *
     * @param context
     * @param type
     * @return
     */
    public List<ResolveInfo> getShareApps(Context context, String type) {
        List<ResolveInfo> mApps;
        Intent intent = new Intent(Intent.ACTION_SEND, null);
        /**
         * 设置Intent种类
         */
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setType(type);

        PackageManager pManager = context.getPackageManager();
        mApps = pManager.queryIntentActivities(intent,
                PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
        return mApps;
    }

    /**
     * get the AppInfo by pkgName
     *
     * @param context
     * @return
     */
    public IAppInfo getCustomizeApp(Context context, String pkgName, String type) {
        if (pkgName == null || pkgName.equals("")) {
            return null;
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setType(type);
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> ls = pm.queryIntentActivities(intent,
                PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);

        boolean hasApp = false;
        ResolveInfo ri = null;
        ActivityInfo ai = null;
        for (int i = 0; i < ls.size(); i++) {
            ri = ls.get(i);
            if (ri != null) {
                ai = ri.activityInfo;
            } else {
                continue;
            }

            if (ai != null) {
                if (ai.packageName != null && ai.packageName.indexOf(pkgName) > -1) {
                    hasApp = true;
                    break;
                }
            }
        }
        if (hasApp && ai != null && ai.packageName != null && ai.name != null) {
            IAppInfo app = new IAppInfo();
            app.setAppIcon(ri.loadIcon(pm));
            app.setAppLauncherClassName(ri.activityInfo.name);
            app.setAppName(ri.loadLabel(pm).toString());
            app.setAppPkgName(ai.packageName);
            return app;
        }
        return null;
    }

    /**
     * share video to requesting app
     *
     * @param ctx
     * @param appInfo
     */
    public void shareImageToCustomizeApp(Context ctx, IAppInfo appInfo,String fileName,String type) {

        if (appInfo != null) {
            File file = new File(fileName);
            Uri imageUri;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                imageUri = FileProvider.getUriForFile(ctx, ctx.getPackageName()+".fileprovider",file);
            } else {
                imageUri = Uri.fromFile(file);
            }
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setComponent(new ComponentName(appInfo.getAppPkgName(),appInfo.getAppLauncherClassName()));
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
             if(type.equals("image")){
                 shareIntent.setType("image/*");
             } else {
                 shareIntent.setType("video/*");
             }
            shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(shareIntent);
        }
    }

    public void shareWithLocal(Context context, String subject, String fileName) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("video/*");
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        File file = new File(fileName);
        Uri imageUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            imageUri = FileProvider.getUriForFile(context, context.getPackageName()+".fileprovider",file);
        } else {
            imageUri = Uri.fromFile(file);
        }
        intent.putExtra(Intent.EXTRA_STREAM, imageUri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Intent intent1 = Intent.createChooser(intent,"Poly Puzzle");
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent1);
    }
    /**
     * check if app installed
     *
     * @param ctx
     * @param intent
     * @return
     */
    public static boolean isAppInstalled(Context ctx, Intent intent) {
        final PackageManager packageManager = ctx.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }
}
