package com.tapque.tools;

import android.graphics.drawable.Drawable;

public class IAppInfo {
    private String appPkgName;
    private String appLauncherClassName;
    private String appName;
    private Drawable appIcon;

    public String getAppPkgName() {
        return appPkgName;
    }

    public void setAppPkgName(String appPkgName) {
        this.appPkgName = appPkgName;
    }

    public String getAppLauncherClassName() {
        return appLauncherClassName;
    }

    public void setAppLauncherClassName(String appLauncherClassName) {
        this.appLauncherClassName = appLauncherClassName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }
}
