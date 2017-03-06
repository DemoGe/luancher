
	package com.netxeon.beeui.activity;

	import android.app.Activity;
	import android.content.Context;
	import android.os.Bundle;
	import android.os.Handler;
	import android.os.Message;
	import android.os.PowerManager;
	import android.os.SystemClock;
	import android.os.SystemProperties;
	import android.util.Log;
	import android.view.KeyEvent;
	import android.view.View;
	import android.view.View.OnClickListener;
	import android.view.View.OnFocusChangeListener;
	import android.view.Window;
	import android.view.WindowManager.LayoutParams;
	import android.widget.ImageView;
	import android.widget.TextView;
	import android.widget.Toast;

	import com.netxeon.beeui.R;

	import java.io.IOException;
	import java.util.Timer;
	import java.util.TimerTask;


	public class ShutdownActivity extends Activity implements OnClickListener ,OnFocusChangeListener{

		
		//private ImageView rebootcorn;
		private ImageView shutdowncorn;
		private ImageView  suspendcorn;
		private	TextView  shutdown;
		private TextView  suspend;
		//private TextView  reboot;
		private final int requestCode = 1;
		private volatile boolean exit = false;

		// msg tag
		private static final int UPDATE_CODE = 1;
		private static final int END_CODE = 2;
		private static final int UPDATE_CODE_delay = 3;
		int i = 0;

		// MytimeTask2 task2;

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			getWindow().setFlags(LayoutParams.FLAG_FULLSCREEN,
					LayoutParams.FLAG_FULLSCREEN);
			setContentView(R.layout.shutdowndialog);
			
			initData();
			
				
			}
	
		

		@Override
		protected void onDestroy() {

		
			super.onDestroy();
		}

		private void initData() {

			
			
			//rebootcorn=(ImageView) findViewById(R.id.rebootcorn);
			shutdowncorn=(ImageView) findViewById(R.id.shutdowncorn);
			suspendcorn=(ImageView) findViewById(R.id.suspendcorn);
			//reboot=(TextView) findViewById(R.id.reboot);
			//rebootcorn.setOnClickListener(this);
			shutdowncorn.setOnClickListener(this);
			suspendcorn.setOnClickListener(this);
			suspendcorn.setFocusable(true);
			suspendcorn.setFocusableInTouchMode(true);
			suspendcorn.requestFocus();
			suspendcorn.requestFocusFromTouch();
            shutdowncorn.setOnFocusChangeListener(this);
            suspendcorn.setOnFocusChangeListener(this);
			shutdowncorn.setImageResource(R.mipmap.shutdown);
			suspendcorn.setImageResource(R.mipmap.standbyhasfocus);

		}

	


		

		
		
		private void sleep5s() {
		
			new MytimeThread().start();

		}

		// 进度条2的线程
		class MytimeThread extends Thread {

			@Override
			public void run() {
				// 设置发消息,//接收新ui
				for (int i = 1; i < 102; i++) {

					try {
						handler.sendEmptyMessage(UPDATE_CODE);
						Log.i("bo", "发消息");
						Thread.sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (!suspendcorn.isFocused()) {

						handler.sendEmptyMessage(END_CODE);
						break;
					}
				}

			}
		};

		Handler handler = new Handler() {

			public void handleMessage(Message msg) {
                   if(msg.what == UPDATE_CODE){
				
			            
					i=0;pause();}
				else if (msg.what == END_CODE) {
					i = 0;
					
				} else if (msg.what == UPDATE_CODE_delay) {
					pause();

				}

			}
		};

		static int time = 0, flag = 0, change = 0;
		Timer timer;
		TimerTask task;

		// 初始时间
		public void resetOrInitTimer(int delay) {

			// 第一次进去
			if (flag == 0) {
				time = delay;
			} else {
				change = 1;
				if (timer == null) {
					Log.i("test", "定时器空");
				}

				if (timer != null) {
					timer.cancel();
					timer = null;
					Log.i("test", "cancel");
				}
				time = delay;
				flag = 0;

			}


		}

		// 开始睡眠
		public void setSleep() {
			if (time == 0) {
				pause();
			} else {

				if (flag == 0) {
					timer = new Timer();
					task = new TimerTask() {

						@Override
						public void run() {

							Message message = new Message();
							message.what = UPDATE_CODE_delay;
							Log.i("bo", "消息---时间的流逝");
							handler.sendEmptyMessage(message.what);
							Log.i("bo", "消息---时间的流逝");

						}
					};

					timer.schedule(task, time * 1000);// 注意毫秒
					flag = 1;
					
				} else {
					Toast.makeText(getApplicationContext(),
							"timer is still running", Toast.LENGTH_SHORT).show();
				}

			}
		}

	
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			switch (v.getId()) {
			case R.id.shutdowncorn:
				if (hasFocus) {
					Log.i("test", hasFocus + "焦点");

					shutdowncorn.setImageResource(R.mipmap.shutdownhasfocus);
				} else {
					shutdowncorn.setImageResource(R.mipmap.shutdown);
					
				}

				break;
			case R.id.suspendcorn:
				if (hasFocus) {
					Log.i("test", hasFocus + "");

					suspendcorn.setImageResource(R.mipmap.standbyhasfocus);
				} else {
					Log.i("test", "line2 un focus");
					suspendcorn.setImageResource(R.mipmap.standby);

					// 失去焦点发结束消息
					handler.sendEmptyMessage(END_CODE);
				}

				break;
		/*	case R.id.rebootcorn:
				if (hasFocus) {
					Log.i("test", hasFocus + "");

					// 改变球边框颜色
					int mycolor = getResources().getColor(
							R.color.text_color_blue);
					
					reboot.setTextColor(mycolor);

				} else {
					Log.i("test", "line3 un focus");
					int mycolor = getResources().getColor(
							R.color.dimgray);
				
					reboot.setTextColor(mycolor);
				}

				break;
*/
			default:
				break;
			}

		}
	



	

		// 关机
		private void shutdown() throws IOException {
			PowerManager   pm =  (PowerManager)getSystemService(Context.POWER_SERVICE);
			//第一个false设置表示关机不要弹出提示框
			pm.shutdown(true,false);
			//finish();
			//直接关没有界面
			//SystemProperties.set("sys.powerctl", "shutdown");

		}


	/*	private void reboot() throws IOException {

			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			pm.reboot(null);
			finish();
		}  */
 
		// 休眠函数
		protected void pause() {
			/**
			 * 锁屏并关闭屏幕
			 */
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			pm.goToSleep(SystemClock.uptimeMillis());
			finish();

		}



		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.shutdowncorn:
				shutdowncorn.setImageResource(R.mipmap.shutdownhasfocus);
		
				try {
					// 关机

					shutdown();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				break;
			case R.id.suspendcorn:
			
		    suspendcorn.setImageResource(R.mipmap.standbyhasfocus);
			
				sleep5s();
				break;
			/*case R.id.rebootcorn:
				
	
				try{
					reboot();
				}catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				break;*/

			default:
				break;
			}
		}
public boolean onKeyDown(int keyCode, KeyEvent event) {


		return super.onKeyDown(keyCode, event);
	};
			
		


}
