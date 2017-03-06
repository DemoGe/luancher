package com.netxeon.beeui.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.netxeon.beeui.R;

/**
 * Created by Administrator on 2017/1/3.
 */

public class SystemVoiceActivity extends Activity implements View.OnClickListener {


    private ImageView systemVoice;
    private  AudioManager audiomanage;
    int numCount=0;
    private boolean isScool=false;
    Button butUp,butDown;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.system_voice);
        systemVoice= (ImageView) findViewById(R.id.system_vocie);
        this.setVolumeControlStream(AudioManager.STREAM_SYSTEM);
        audiomanage = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int max = audiomanage.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
        numCount=max;
        int currentVolume =audiomanage
                .getStreamVolume(AudioManager.STREAM_SYSTEM);
        getImage(currentVolume);
        IntentFilter filter = new IntentFilter(
                "android.media.VOLUME_CHANGED_ACTION");
        registerReceiver(receiver, filter);
        butUp= (Button) findViewById(R.id.volume_up);
        butDown= (Button) findViewById(R.id.volume_down);
        butUp.setOnClickListener(this);
        butDown.setOnClickListener(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);


    }

    private void getImage(int vol) {
        switch (vol) {
            case 0:
//			imageView.setBackgroundResource(resid)
                systemVoice.setBackgroundResource(R.mipmap.w1);
                break;
            case 1:
                systemVoice.setBackgroundResource(R.mipmap.w2);
                break;
            case 2:
                systemVoice.setBackgroundResource(R.mipmap.w3);
                break;
            case 3:
                systemVoice.setBackgroundResource(R.mipmap.w4);
                break;
            case 4:
                systemVoice.setBackgroundResource(R.mipmap.w5);
                break;
            case 5:
                systemVoice.setBackgroundResource(R.mipmap.w6);
                break;
            case 6:
                systemVoice.setBackgroundResource(R.mipmap.w7);
                break;
            case 7:
                systemVoice.setBackgroundResource(R.mipmap.w8);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
         handler.removeMessages(1);
         handler.sendEmptyMessageDelayed(1,3000);
        int currentVolume = audiomanage
                .getStreamVolume(AudioManager.STREAM_SYSTEM); // 获取当前值
        Log.e(MainActivity.class.getName(), "onKeyDown currentVolume="
                + currentVolume);
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            if (currentVolume < numCount) {
                currentVolume = currentVolume + 1;
            }
            audiomanage.adjustStreamVolume(AudioManager.STREAM_SYSTEM,
                    AudioManager.ADJUST_RAISE, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
//			seekBar.setProgress(currentVolume);
            getImage(currentVolume);
            return true;

        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            // todo
            if (currentVolume > 0) {
                currentVolume = currentVolume - 1;
            }
            audiomanage.adjustStreamVolume(AudioManager.STREAM_SYSTEM,
                    AudioManager.ADJUST_LOWER, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
//			seekBar.setProgress(currentVolume);
            getImage(currentVolume);
            return true;

        }
        return super.onKeyDown(keyCode, event);
    }

    private BroadcastReceiver receiver =new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("android.media.VOLUME_CHANGED_ACTION"
                    .equals(intent.getAction())&&!isScool) {

                int type = intent.getIntExtra(
                        "android.media.EXTRA_VOLUME_STREAM_TYPE", 0);
                int index = intent.getIntExtra(
                        "android.media.EXTRA_VOLUME_STREAM_VALUE", 0);
                int oldIndex = intent.getIntExtra(
                        "android.media.EXTRA_PREV_VOLUME_STREAM_VALUE", 0);
				//seekBar.setProgress(index);
                Log.e(MainActivity.class.getName(), "type=" + type
                        + "   index=" + index + "    oldIndex=" + oldIndex);}
        }
    };

    @Override
    public void onClick(View v) {
        handler.removeMessages(1);
        handler.sendEmptyMessageDelayed(1,3000);
        int currentVolume = audiomanage
                .getStreamVolume(AudioManager.STREAM_SYSTEM); // 获取当前值
          switch (v.getId()){
              case R.id.volume_up:
                      if (currentVolume < numCount) {
                          currentVolume = currentVolume + 1;
                      }
                      audiomanage.adjustStreamVolume(AudioManager.STREAM_SYSTEM,
                              AudioManager.ADJUST_RAISE, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
                      getImage(currentVolume);
                  break;
              case R.id.volume_down:
                  if (currentVolume > 0) {
                      currentVolume = currentVolume - 1;
                  }
                  audiomanage.adjustStreamVolume(AudioManager.STREAM_SYSTEM,
                          AudioManager.ADJUST_LOWER, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
//			seekBar.setProgress(currentVolume);
                  getImage(currentVolume);
                  break;

          }
    }
    Handler handler =new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what) {
                case 1://关闭
                    finish();
                    break;

                default:
                    break;
            }
        }

    };


}