package com.netxeon.beeui.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;

import com.netxeon.beeui.R;
import com.netxeon.beeui.adapter.AppActivityAdapter;
import com.netxeon.beeui.bean.App;
import com.netxeon.beeui.utils.MyGridView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2016/12/31.
 */
public class AppActicity extends Activity implements AdapterView.OnItemLongClickListener {
    private MyGridView gv_shortcut_main;
    private AppActivityAdapter scAdapter;
    List<App> shortCutApps = new ArrayList<>();
    BroadcastReceiver receiver;
    final Context mContext = this;
    Thread bgThread = new Thread(new Runnable() {
        @Override
        public void run() {
            shortCutApps.clear();
            shortCutApps.addAll(getAPPs(mContext));
            handler.sendEmptyMessage(1);
        }
    });
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
//                shortCutApps.clear();
//                shortCutApps.addAll(getAPPs(AppActicity.this));
//                scAdapter.mData = shortCutApps;
                scAdapter.notifyDataSetChanged();
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_activity);
//        shortCutApps = getAPPs(this);
        bgThread.start();
        gv_shortcut_main = (MyGridView) findViewById(R.id.gv_shortcut_main);
        scAdapter = new AppActivityAdapter(AppActicity.this, shortCutApps, R.layout.appactivity_item);
        gv_shortcut_main.setAdapter(scAdapter);
        gv_shortcut_main.setOnItemLongClickListener(this);
        gv_shortcut_main.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {//没有用到的
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                scAdapter.notifyDataSetChanged(position);
                if (gv_shortcut_main != null && view != null) {
                    gv_shortcut_main.setTopView(view);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        gv_shortcut_main.setOnFocusChangeListener(new View.OnFocusChangeListener() {//焦点显示
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (gv_shortcut_main != null) {
                        startFrameAnim(gv_shortcut_main);
                    }
                } else {
                    //gv_shortcut_main.setSelector(getResources().getDrawable(R.drawable.lock_input_back));

                }
            }
        });
        gv_shortcut_main.setOnItemClickListener(new AdapterView.OnItemClickListener() {//点击事件
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                scAdapter.notifyDataSetChanged(position);
                if (gv_shortcut_main != null && view != null) {
                    gv_shortcut_main.setTopView(view);
                }
                try {
                    Intent mainintent = new Intent(Intent.ACTION_MAIN, null);
                    mainintent.addCategory(Intent.CATEGORY_LAUNCHER);
                    mainintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                    mainintent.setComponent(shortCutApps.get(position).getIntent().getComponent());
                    startActivity(mainintent);
//                    Intent intent = shortCutApps.get(position).getIntent();
//                    startActivity(intent);
                }catch (Exception e){
                    Log.e("tag",e+"");
                }

            }
        });
        InstallReceiver();
    }

    private void startFrameAnim(View view) {
        ScaleAnimation animation = new ScaleAnimation(0.95f, 1f, 0.95f, 1f
                , Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(90);
        animation.setStartTime(0);
        view.startAnimation(animation);
    }


    private void InstallReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
//                handler.sendEmptyMessageDelayed(1, 50);
                bgThread.run();
            }
        };
        IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        filter.addDataScheme("package");
        registerReceiver(receiver, filter);

    }


    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();

    }

    /**
     * 获取app的信息并自己封装一个新的类
     *
     * @param context
     * @return
     */
    public static List<App> getAPPs(Context context) {
        List<App> list = new ArrayList<App>();
        PackageManager manager = context.getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        final List<ResolveInfo> apps = manager.queryIntentActivities(mainIntent, 0);
        Collections.sort(apps, new ResolveInfo.DisplayNameComparator(manager));

        if (apps != null) {
            for (ResolveInfo info : apps) {
                App app = new App();
                Drawable icon = info.loadIcon(manager);
                String activityName = info.activityInfo.name;
                String pkgName = info.activityInfo.applicationInfo.packageName;
                String appName = (String) info.loadLabel(manager);
                app.setIcon(icon);
                app.setAppName(appName);
                Intent launchIntent = new Intent();
                launchIntent.setComponent(new ComponentName(pkgName, activityName));
                app.setLauncherActivity(activityName);
                app.setIntent(launchIntent);
                app.setPackageName(info.activityInfo.applicationInfo.packageName);
                list.add(app);

            }
        }

        return list;
    }


    public void unInstallApplication(String packageName) {
        Intent intent = new Intent(Intent.ACTION_DELETE);
        intent.setData(Uri.parse("package:" + packageName));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        try {
            startActivity(intent);
        } catch (Exception e) {
            Log.e("error", "AppsActivity.unInstallApplication() uninstall failed: " + packageName);
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        unInstallApplication(shortCutApps.get(position).getPackageName());
//        scAdapter.notifyDataSetChanged();
        if (bgThread != null) {
            bgThread.run();
        } else {
            bgThread.start();
        }
        return true;
    }


}