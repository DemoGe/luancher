package com.netxeon.beeui.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.netxeon.beeui.R;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;


public class MemoryCleaner {
    private Activity mActivity;

    private MySinkingView mySinkingView;
    private int percent;

    private boolean isUpFinish = false;


    private static final int cleanfinish = 0;
    private static final int cleanstart = 1;
    private static final int upfinish = 2;
    private  static  final  int downfinish =3;
    private  static  final int delaytime =1000;


    public MemoryCleaner(Activity activity) {
        mActivity = activity;
        initViews();
        handler.sendEmptyMessage(1);

    }

    private void updateMemory() {

        long tatal = getTotalMemory();
        long available = getAvailMemory();

        float result = (float) (tatal - available) / (float) (tatal);

//        Log.i("bo", "tatal=" + tatal + "availale=" + available + "result=" + result);
        if (mySinkingView != null) {
            mySinkingView.setPercent((float) result);
        }
        loadui();

    }

    private void downUi() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (isUpFinish) {
                        long tatal = getTotalMemory();
                        long available = getAvailMemory();

                        float result = (float) (tatal - available) / (float) (tatal);

 //                       Log.i("bo", "tatal=" + tatal + "availale=" + available + "result=" + result);

                        if (percent == 100) {
              //              Log.i("bo", "100percent:" + percent);
                            while (percent >= (result * 100)) {
              //                  Log.i("bo", percent + "result*100");
                                mySinkingView.setPercent((float) percent / 100);
                                percent -= 1;
                                try {
                                    Thread.sleep(20);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        int i= (int) ((tatal-available)/(float)tatal * 100)+1;
                        Intent intent =new Intent();
                        intent.putExtra("data",String.valueOf(i));
                        mActivity.setResult(2,intent);
                        break;

                    } else {
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                handler.sendEmptyMessage(downfinish);
            }
        }).start();

    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case cleanfinish://清理完成
                    downUi();
                    break;

                case cleanstart:
                    updateMemory();
                    cleanMemory();
                    break;

                case upfinish://动画上去完成
                    isUpFinish = true;
                    break;
                case downfinish:
                    Toast.makeText(mActivity, R.string.clear_memory_done, Toast.LENGTH_LONG).show();

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                          mActivity.finish();
                        }
                    },delaytime);
                    break;
            }
        }
    };

    public void cleanMemory() {
        new Thread() {
            @Override
            public void run() {
                ActivityManager activityManger = (ActivityManager) mActivity.getSystemService(Context.ACTIVITY_SERVICE);
                List<ActivityManager.RunningAppProcessInfo> list = activityManger.getRunningAppProcesses();
                if (list != null) {
                    for (int i = 0; i < list.size(); i++) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        ActivityManager.RunningAppProcessInfo apinfo = list.get(i);
                        String[] pkgList = apinfo.pkgList;
                        if (apinfo.importance > ActivityManager.RunningAppProcessInfo.IMPORTANCE_SERVICE) {
                            for (int j = 0; j < pkgList.length; j++) {
                                activityManger.killBackgroundProcesses(pkgList[j]);
                            }
                        }
                    }
                }
                handler.sendEmptyMessage(cleanfinish);
            }
        }.start();

    }


    /**
     * 向上动画
     */
    private void loadui() {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {

                while (percent < 100) {
             //       Log.i("bo", percent + "");
                    percent += 1;
                    mySinkingView.setPercent((float) percent / 100);
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
                handler.sendEmptyMessage(upfinish);
            }
        });
        thread.start();
    }


    /**
     * 可用空间
     *
     * @return
     */
    private long getAvailMemory() {
        ActivityManager am = (ActivityManager) mActivity.getSystemService(Context.ACTIVITY_SERVICE);
        MemoryInfo mi = new MemoryInfo();
        am.getMemoryInfo(mi);
        return mi.availMem / (1024 * 1024);
    }

    /**
     * 总使用空间
     *
     * @return
     */
    private long getTotalMemory() {
        String str1 = "/proc/meminfo";
        String str2;
        String[] arrayOfString;
        long initial_memory = 0;

        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
            str2 = localBufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
            initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;
            localBufferedReader.close();

        } catch (IOException e) {
        }
        return initial_memory / (1024 * 1024);
    }

    private void initViews() {
        mySinkingView = (MySinkingView) mActivity.findViewById(R.id.sinking);

    }

}
