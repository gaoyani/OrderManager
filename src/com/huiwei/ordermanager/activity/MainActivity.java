/*****************************************************
 * Copyright(c)2014-2015 北京汇为永兴科技有限公司
 * MainActivity.java
 * 创建人：高亚妮
 * 日     期：2014-6-20
 * 描     述：主页面文件
 * 版     本：v6.0
 *****************************************************/
package com.huiwei.ordermanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huiwei.commonlib.Preferences;
import com.huiwei.ordermanager.R;
import com.huiwei.ordermanager.adapter.MainMenuItemAdapter;
import com.huiwei.ordermanager.baseclass.BaseActivity;
import com.huiwei.ordermanager.baseclass.BaseFragment.OnMenuPopListener;
import com.huiwei.ordermanager.common.SysApplication;
import com.huiwei.ordermanager.constant.CommonConstant;
import com.huiwei.ordermanager.constant.UrlConstant;
import com.huiwei.ordermanager.task.CategoryTask;
import com.huiwei.ordermanager.task.LogoutTask;
import com.huiwei.ordermanager.task.SyncMenuTask;

public class MainActivity extends BaseActivity {

    private final static int ANIMATION_DURATION_FAST = 350;
    private final static int ANIMATION_DURATION_SLOW = 250;
    private final static int MOVE_DISTANCE = 10;

	private boolean slided = false;
	private int screenWidth;
	private float mPositionX;

	private int tabIndex = 0;
	private ListView mainMenuListView;
	private RelativeLayout layoutTab;
	private LinearLayout layoutContent;
	private TextView waiterName;
	
	private MainMenuItemAdapter adapter;

	private OrderFragment fragmentOrder;
//	private MenuFragment fragmentMenu;
	private MoreInfoFragment fragmentMore;
	private FragmentManager fragmentManager;
	private FragmentTransaction fragmentTransaction;
	
	private long mExitTime;
	private static final int INTERVAL = 2000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (Preferences.GetBoolean(this, "keep_screen_on", true)) {
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); 
		}
		
		setContentView(R.layout.activity_main);
		
		initMainMenu();
		initFragment();
		
		waiterName = (TextView)findViewById(R.id.tv_waitress);
		waiterName.setText(Preferences.GetString(this, "user"));
		
		layoutTab = (RelativeLayout)findViewById(R.id.layout_tab);
		layoutContent = (LinearLayout)findViewById(R.id.layout_main);
		layoutContent.setOnTouchListener(mOnTouchListener);
		
		screenWidth = getResources().getDisplayMetrics().widthPixels;
		
		syncMenu();
	}
	
	private void syncMenu() {
		CategoryTask ct = new CategoryTask(this, null);
		ct.execute(UrlConstant.getServerUrl(this));
		
		SyncMenuTask smt = new SyncMenuTask(this, null);
		smt.execute(UrlConstant.getServerUrl(this));;
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}

	/*****************************************************
	 * 函数名：initMainMenu
	 * 输     入：无
	 * 输     出：无
	 * 描     述：初始化主菜单
	 * 调用接口：onCreate
	 * 创建人：高亚妮
	 * 日     期：2014-6-20
	 *****************************************************/
	private void initMainMenu() {
		mainMenuListView = (ListView)findViewById(R.id.lv_menu);
		adapter = new MainMenuItemAdapter(this);
		mainMenuListView.setAdapter(adapter);
		mainMenuListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				adapter.setSelectItemID(position);
				adapter.notifyDataSetChanged();
				
				fragmentTransaction = fragmentManager.beginTransaction();
				switch (position) {
				case 0:
//					fragmentTransaction.hide(fragmentMenu);
					fragmentTransaction.hide(fragmentMore);
		            fragmentTransaction.show(fragmentOrder);    
					break;
//				case 1:
//					fragmentTransaction.hide(fragmentOrder);
//					fragmentTransaction.hide(fragmentMore);
//					fragmentTransaction.show(fragmentMenu);
//					break;
				case 1:
//					fragmentTransaction.hide(fragmentMenu);
					fragmentTransaction.hide(fragmentOrder);
					fragmentTransaction.show(fragmentMore);
					break;
					
				default:
					break;
				}
				
				if (position != tabIndex) {
					fragmentTransaction.commit();
					tabIndex = position;
				}
				
				slideIn();
			}
		});
	}
	
	private void initFragment() {
		fragmentOrder = new OrderFragment();
//		fragmentMenu = new MenuFragment();
		fragmentMore = new MoreInfoFragment();
		fragmentOrder.setOnMenuPopListener(menuPopListener);
//		fragmentMenu.setOnMenuPopListener(menuPopListener);
		fragmentMore.setOnMenuPopListener(menuPopListener);

		fragmentManager = getSupportFragmentManager();
		fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.add(R.id.layout_content, fragmentOrder);
//		fragmentTransaction.add(R.id.layout_content, fragmentMenu);
		fragmentTransaction.add(R.id.layout_content, fragmentMore);
		fragmentTransaction.show(fragmentOrder);  
		fragmentTransaction.commit();
	}
	
	OnMenuPopListener menuPopListener = new OnMenuPopListener() {
		
		@Override
		public void onMenuPop() {
			if (slided) {
				slideIn();
			} else {
				slideOut();
			}
		}
	};

	private OnTouchListener mOnTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (v.getId() == R.id.layout_main) {
				int action = event.getAction();
				switch (action) {
				case MotionEvent.ACTION_DOWN:
					mPositionX = event.getX();
					break;
				case MotionEvent.ACTION_MOVE:
					final float currentX = event.getX();
					if (currentX - mPositionX <= -MOVE_DISTANCE && !slided) {
						slideOut();
					} else if (currentX - mPositionX >= MOVE_DISTANCE
							&& slided) {
						slideIn();
					}
					break;
				}
				return true;
			}
			return false;
		}
	};

	private void slideOut() {
		TranslateAnimation translate = new TranslateAnimation(-layoutTab.getWidth(),
				0, 0, 0);
		translate.setDuration(ANIMATION_DURATION_SLOW);
		translate.setFillAfter(true);
		layoutTab.startAnimation(translate);
		layoutTab.getAnimation().setAnimationListener(
				new Animation.AnimationListener() {

					@Override
					public void onAnimationStart(Animation anim) {
						
					}

					@Override
					public void onAnimationRepeat(Animation animation) {

					}

					@Override
					public void onAnimationEnd(Animation anima) {
						slided = true;
//						layoutTab.clearAnimation();
					}
				});
		TranslateAnimation animation = new TranslateAnimation(
				0, layoutTab.getWidth(), 0, 0);
		animation.setDuration(ANIMATION_DURATION_FAST);
		animation.setFillAfter(true);
		layoutContent.startAnimation(animation);
		layoutContent.getAnimation().setAnimationListener(
				new Animation.AnimationListener() {

					@Override
					public void onAnimationStart(Animation anim) {
						
					}

					@Override
					public void onAnimationRepeat(Animation animation) {

					}

					@Override
					public void onAnimationEnd(Animation anima) {
						layoutContent.clearAnimation();
						RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams
								(screenWidth, LayoutParams.MATCH_PARENT);
						lp.setMargins(layoutTab.getWidth(), 0, -layoutTab.getWidth(), 0);
						layoutContent.setLayoutParams(lp);
					}
				});
	}

	/**
	 * 婊戣繘渚ц竟鏍�
	 */
	private void slideIn() {
		TranslateAnimation translate = new TranslateAnimation(
				0, -layoutTab.getWidth(), 0, 0);
		translate.setDuration(ANIMATION_DURATION_FAST);
		translate.setFillAfter(true);
		layoutTab.clearAnimation();
		layoutTab.startAnimation(translate);
		layoutTab.getAnimation().setAnimationListener(
				new Animation.AnimationListener() {

					@Override
					public void onAnimationStart(Animation animation) {
						
					}

					@Override
					public void onAnimationRepeat(Animation animation) {

					}

					@Override
					public void onAnimationEnd(Animation animation) {
						slided = false;
						
					}
				});
		
		TranslateAnimation mainAnimation = new TranslateAnimation(
				0, -layoutTab.getWidth(), 0, 0);
		mainAnimation.setDuration(ANIMATION_DURATION_SLOW);
		mainAnimation.setFillAfter(true);
		layoutContent.clearAnimation();
		layoutContent.startAnimation(mainAnimation);
		layoutContent.getAnimation().setAnimationListener(
				new Animation.AnimationListener() {

					@Override
					public void onAnimationStart(Animation anim) {
						
					}

					@Override
					public void onAnimationRepeat(Animation animation) {

					}

					@Override
					public void onAnimationEnd(Animation anima) {
						layoutContent.clearAnimation();
						RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams
								(screenWidth, LayoutParams.MATCH_PARENT);
						lp.setMargins(0, 0, 0, 0);
						layoutContent.setLayoutParams(lp);
					}
				});
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (slided) {
			slideIn();
		} else {
			slideOut();
		}
		return true;
	}

//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			exit();
//			return true;
//		}
//		return super.onKeyDown(keyCode, event);
//	}
	
	@Override
	public void onBackPressed() {
		if (System.currentTimeMillis() - mExitTime > INTERVAL) {
			Toast.makeText(this, getResources().getString(R.string.exit_app), 
					Toast.LENGTH_SHORT).show();
			mExitTime = System.currentTimeMillis();
		} else {
			LogoutTask at = new LogoutTask(this, null);
			at.execute(UrlConstant.getLogoutUrl(this));
			
			finish();
			System.exit(0);
		}
	}
	
	// logout handler
		Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				Message msg_end = new Message();
				int flag = msg.what;
				msg_end.what = CommonConstant.DATA_LOADING_END_STATUS;
//				getActivity().baseHandler.handleMessage(msg_end);
				if (flag == CommonConstant.SUCCESS) {
					Toast.makeText(MainActivity.this, 
							getResources().getString(R.string.logout_success),
							Toast.LENGTH_LONG).show();
					finish();
//					System.exit(0);
				} else if (flag == CommonConstant.OTHER_FAIL) {
					Toast.makeText(MainActivity.this, 
							getResources().getString(R.string.logout_failed),
							Toast.LENGTH_LONG).show();
				} else if (flag == CommonConstant.CONNECT_SERVER_FAIL) {
					Toast.makeText(MainActivity.this, 
							getResources().getString(R.string.connect_server_failed),
							Toast.LENGTH_SHORT).show();
				}

			}
		};
	
	@Override
	public void onDestroy() {

		super.onDestroy();
	}

}
