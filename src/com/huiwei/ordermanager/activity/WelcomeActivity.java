package com.huiwei.ordermanager.activity;

import java.util.Timer;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.WindowManager;

import com.huiwei.commonlib.CommonFunction;
import com.huiwei.commonlib.NetCheck;
import com.huiwei.commonlib.Preferences;
import com.huiwei.ordermanager.R;

public class WelcomeActivity extends Activity {
	private Timer mTimer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (Preferences.GetBoolean(this, "keep_screen_on", true)) {
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); 
		}
		
		setContentView(R.layout.activity_welcome);
		
//		WindowManager windowManager = getWindowManager();  
//        Display display = windowManager.getDefaultDisplay();  
//        int screenWidth = display.getWidth();  
//        int screenHeight = display.getHeight(); 
        
		new Handler().postDelayed(r, 2000);// 
		
		String packname = "com.safemyphone.server";
		try {
		    ComponentName componentName = new ComponentName(packname,packname + ".AppStartServer");
		    Intent intent = new Intent();
		    intent.setComponent(componentName);
		    intent.setAction(Intent.ACTION_MAIN);
		    startService(intent);
		} catch (Exception e) {
		}
	};
	
	Runnable r = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			intent.setClass(WelcomeActivity.this, UserLoginActivity.class);
//			intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			startActivity(intent);
			finish();
		}
	};
	
//	private void startTimer() {
//		mTimer = new Timer();       
//		mTimer.schedule(new TimerTask() {           
//		            @Override
//		            public void run() {
//		            	jumpToLogin();
//		            	mTimer.cancel();
//		            }
//		        }, 5*1000, 5*1000); 
//	}
//	
//	private void jumpToLogin() {
//		Intent intent = new Intent();
//		intent.setClass(this, UserLoginActivity.class);
//		startActivity(intent);
//	}

	@Override
	protected void onResume() {
		super.onResume();
	}
}
