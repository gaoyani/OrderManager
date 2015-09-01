package com.huiwei.ordermanager.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.huiwei.commonlib.Preferences;
import com.huiwei.ordermanager.R;
import com.huiwei.ordermanager.baseclass.BaseActivity;

public class AllDishesActivity extends BaseActivity {

	private MenuFragment fragmentMenu;
	private FragmentManager fragmentManager;
	private FragmentTransaction fragmentTransaction;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Preferences.GetBoolean(this, "keep_screen_on", true)) {
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); 
		}
		
		setContentView(R.layout.activity_all_dishes);
		
		initFragment();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
	
	private void initFragment() {
		fragmentMenu = new MenuFragment();
		fragmentMenu.setFlag(false);
		fragmentManager = getSupportFragmentManager();
		fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.replace(R.id.layout_content, fragmentMenu);
		fragmentTransaction.commit();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			fragmentMenu.returnBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
