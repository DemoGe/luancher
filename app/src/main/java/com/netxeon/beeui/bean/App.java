package com.netxeon.beeui.bean;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.drawable.Drawable;

/**
 * Created by Symphony on 2016/7/14.
 * 工具类不用改
 */
public class App {
    private Drawable icon;
    private String appName;
    private Intent intent;
    private String launcherActivity;
    private String packageName;
    private int status;
    private ComponentName componentName;
    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Intent getIntent() {
        return intent;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getLauncherActivity() {
        return launcherActivity;
    }

    public void setLauncherActivity(String launcherActivity) {
        this.launcherActivity = launcherActivity;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    public ComponentName getComponentName() {
        return componentName;
    }
    public void setComponentName(ComponentName componentName) {
        this.componentName = componentName;
    }

}
