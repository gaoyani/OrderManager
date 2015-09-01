package com.huiwei.ordermanager.view;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.StaticLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.huiwei.ordermanager.R;
import com.huiwei.ordermanager.info.DishesInfo;
import com.huiwei.ordermanager.info.PreferInfo;

public class LoadingProgressView extends RelativeLayout {

	public static final int RELOADING = 0;
	
	private Context mContext;
	private Handler handler;
	private ProgressBar pb;
	private RelativeLayout layoutFailed;
	
	public LoadingProgressView(Context context) {
		super(context);
		mContext = context;
	}
	
	public LoadingProgressView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}
	
	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();
		
		pb = (ProgressBar)findViewById(R.id.pb);
		layoutFailed = (RelativeLayout)findViewById(R.id.layout_update_failed);
		
		
		if (layoutFailed != null) {
			layoutFailed.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					layoutFailed.setVisibility(View.GONE);
					pb.setVisibility(View.VISIBLE);
					handler.sendEmptyMessage(RELOADING);
				}
			});
		}
	}
	
	public void setHandler(Handler handler) {
		this.handler = handler;
	}
	
	public void showProgressBar() {
		pb.setVisibility(View.VISIBLE);
		layoutFailed.setVisibility(View.GONE);
	}
	
	public void showLoadingFailed() {
		pb.setVisibility(View.GONE);
		layoutFailed.setVisibility(View.VISIBLE);
	}
	
	public void hideView() {
		pb.setVisibility(View.GONE);
		layoutFailed.setVisibility(View.GONE);
	}
}
