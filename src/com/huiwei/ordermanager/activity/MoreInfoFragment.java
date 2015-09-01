package com.huiwei.ordermanager.activity;

import com.huiwei.commonlib.NetCheck;
import com.huiwei.commonlib.Preferences;
import com.huiwei.ordermanager.R;
import com.huiwei.ordermanager.baseclass.BaseFragment;
import com.huiwei.ordermanager.common.SysApplication;
import com.huiwei.ordermanager.constant.CommonConstant;
import com.huiwei.ordermanager.constant.UrlConstant;
import com.huiwei.ordermanager.task.LogoutTask;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MoreInfoFragment extends BaseFragment {

	private static final String TAG = "Activity";
	private Button logout;
	private TextView waiterId;
	private TextView version;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_more_info, container, false);
		((ImageView)view.findViewById(R.id.iv_menu)).setOnClickListener(
				new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				listener.onMenuPop();
			}
		});
		
		instantiation(view);
		
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	private void instantiation(View view) {
		waiterId = (TextView) view.findViewById(R.id.tv_waiter);
		waiterId.setText(Preferences.GetString(
				getActivity().getApplicationContext(),"user").toString());
		logout = (Button) view.findViewById(R.id.btn_quit);
		logout.setOnClickListener(new mylogoutListener());
		
		version = (TextView) view.findViewById(R.id.more_version);
		version.setText(getResources().getString(R.string.app_name)+"v"+
				SysApplication.getAppVersionName(getActivity().getApplicationContext()));
	}

	private class mylogoutListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			if (NetCheck.checkNet(getActivity())) {
				Message msg_end = new Message();
				msg_end.what = CommonConstant.DATA_LOADING_START_STATUS;
//				baseHandler.handleMessage(msg_end);
				LogoutTask at = new LogoutTask(
						getActivity(), handler);

				at.execute(UrlConstant.getLogoutUrl(getActivity()));

			} else {
				Toast.makeText(getActivity().getApplicationContext(), 
						getResources().getString(R.string.no_network),
						Toast.LENGTH_LONG).show();
			}

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
//			getActivity().baseHandler.handleMessage(msg_end);
			if (flag == CommonConstant.SUCCESS) {
				Toast.makeText(getActivity().getApplicationContext(), 
						getResources().getString(R.string.logout_success),
						Toast.LENGTH_LONG).show();
				Intent intent = new Intent();
				intent.setClass(getActivity(), UserLoginActivity.class);
				startActivity(intent);
				getActivity().finish();
			} else if (flag == CommonConstant.OTHER_FAIL) {
				Toast.makeText(getActivity().getApplicationContext(), 
						getResources().getString(R.string.logout_failed),
						Toast.LENGTH_LONG).show();
			} else if (flag == CommonConstant.CONNECT_SERVER_FAIL) {
				Toast.makeText(getActivity().getApplicationContext(), 
						getResources().getString(R.string.connect_server_failed),
						Toast.LENGTH_SHORT).show();
			}

		}
	};
}
