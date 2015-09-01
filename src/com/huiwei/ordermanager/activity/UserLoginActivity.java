package com.huiwei.ordermanager.activity;

import com.huiwei.commonlib.NetCheck;
import com.huiwei.commonlib.Preferences;
import com.huiwei.ordermanager.R;
import com.huiwei.ordermanager.baseclass.BaseActivity;
import com.huiwei.ordermanager.common.SysApplication;
import com.huiwei.ordermanager.constant.CommonConstant;
import com.huiwei.ordermanager.constant.UrlConstant;
import com.huiwei.ordermanager.task.LoginTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.renderscript.Program;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class UserLoginActivity extends Activity {
	private EditText userName = null;
	private EditText password = null;
	private EditText serverTT = null;
	private Button login = null;
	private ProgressBar pb;
	private CheckBox keepScreenOn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		instantiation();
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			int flag = msg.what;
			if (flag == CommonConstant.SUCCESS) {
				Toast.makeText(getApplicationContext(), 
						getResources().getString(R.string.login_success),
						Toast.LENGTH_SHORT).show();
				Intent intent = new Intent();
				intent.setClass(UserLoginActivity.this, MainActivity.class);
				startActivity(intent);
				UserLoginActivity.this.finish();
			} else if (flag == CommonConstant.LOGIN_FAIL_LOGINED) {
				Toast.makeText(getApplicationContext(), 
						getResources().getString(R.string.login_logined),
						Toast.LENGTH_SHORT).show();
			} else if (flag == CommonConstant.OTHER_FAIL) {
				Toast.makeText(getApplicationContext(), 
						getResources().getString(R.string.login_failed),
						Toast.LENGTH_SHORT).show();
			} else if (flag == CommonConstant.EXCEPTION_FAIL) {
				Toast.makeText(getApplicationContext(), 
						getResources().getString(R.string.login_exception_fail),
						Toast.LENGTH_SHORT).show();
			}
			
			pb.setVisibility(View.GONE);
		}
	};

	public void instantiation() {
		userName = (EditText) findViewById(R.id.et_user_name);
		serverTT = (EditText) findViewById(R.id.et_server_ip);
		password = (EditText) findViewById(R.id.et_password);
		pb = (ProgressBar) findViewById(R.id.pb);
		String serverID = Preferences.GetString(this, "server_ID");
		if (serverID != null && !serverID.equals("")) {
			serverTT.setText(serverID);
		}
		
		String name = Preferences.GetString(this, "user");
		if (name != null && !name.equals("")) {
			userName.setText(name);
			password.setText(Preferences.GetString(this, "password"));
		}

		login = (Button) findViewById(R.id.ib_login);
		login.setOnClickListener(new MyLoginlistener());
		
		keepScreenOn = (CheckBox) findViewById(R.id.cb_keep_screen_on);
	}

	private class MyLoginlistener implements OnClickListener {

		@Override
		public void onClick(View v) {
			if (userName.getText().length() == 0) {
				Toast.makeText(getApplicationContext(), 
						getResources().getString(R.string.user_name_empty),
						Toast.LENGTH_SHORT).show();
			} else {
				if (password.getText().length() == 0) {
					Toast.makeText(getApplicationContext(), 
							getResources().getString(R.string.password_empty),
							Toast.LENGTH_SHORT).show();
				} else {
					if (NetCheck.checkNet(UserLoginActivity.this)) {
						pb.setVisibility(View.VISIBLE);
						Preferences.SetString(UserLoginActivity.this,
								"server_ID", serverTT.getText().toString());
						Preferences.SetString(UserLoginActivity.this,
								"user", userName.getText().toString());
						Preferences.SetString(UserLoginActivity.this,
								"password", password.getText().toString());
						
						Preferences.SetBoolean(UserLoginActivity.this, "keep_screen_on", keepScreenOn.isChecked());

						LoginTask at = new LoginTask(UserLoginActivity.this,
								handler, userName.getText().toString(),
								password.getText().toString());
		
						at.execute(UrlConstant.getLoginUrl(UserLoginActivity.this));
					} else {
						Toast.makeText(getApplicationContext(), 
								getResources().getString(R.string.no_network),
								Toast.LENGTH_SHORT).show();
					}

				}
			}

		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
}
