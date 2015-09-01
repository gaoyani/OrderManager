package com.huiwei.ordermanager.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huiwei.ordermanager.R;
import com.huiwei.ordermanager.info.DishesInfo;
import com.huiwei.ordermanager.info.PreferInfo;

public class PreferChooseView extends RelativeLayout implements OnClickListener {

	private Context mContext;
	
	private List<CheckBox> ckList = new ArrayList<CheckBox>();
	private EditText otherPrefer;
	private TextView historyPrefer;
	private Button ok, cancel;
	
	private DishesInfo dishesInfo;
	private Handler handler;
	
	public PreferChooseView(Context context) {
		super(context);
		mContext = context;
	}
	
	public PreferChooseView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}
	
	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();
		
		ckList.add((CheckBox) findViewById(R.id.checkBox1));
		ckList.add((CheckBox) findViewById(R.id.checkBox2));
		ckList.add((CheckBox) findViewById(R.id.checkBox3));
		ckList.add((CheckBox) findViewById(R.id.checkBox4));
		ckList.add((CheckBox) findViewById(R.id.checkBox5));
		ckList.add((CheckBox) findViewById(R.id.checkBox6));
		otherPrefer = (EditText) findViewById(R.id.et_other);
		historyPrefer = (TextView) findViewById(R.id.tv_history);
		ok = (Button) findViewById(R.id.btn_ok);
		cancel = (Button) findViewById(R.id.btn_cancel);
		
		if (ok != null) {
			ok.setOnClickListener(this);
			cancel.setOnClickListener(this);
		}
	}
	
	public void setData(DishesInfo dishesInfo, Handler handler) {
		this.handler = handler;
		this.dishesInfo = dishesInfo;
		for (CheckBox ckBox : ckList) {
			ckBox.setVisibility(View.GONE);
		}
		
		for (int i=0; i<dishesInfo.preferList.size(); i++) {
			PreferInfo info = dishesInfo.preferList.get(i);
			CheckBox ckBox = ckList.get(i);
			ckBox.setText(info.name);
			ckBox.setVisibility(View.VISIBLE);
			ckBox.setChecked(info.isChecked);
		}
		
		otherPrefer.setText(dishesInfo.otherPrefer);
		if (dishesInfo.preferHistory == null || dishesInfo.preferHistory.length() == 0) {
			historyPrefer.setVisibility(View.GONE);
		} else {
			historyPrefer.setVisibility(View.VISIBLE);
			historyPrefer.setText(mContext.getResources().getString(R.string.prefer_history)+
					dishesInfo.preferHistory);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_ok: {
			for (int i=0; i<dishesInfo.preferList.size(); i++) {
				PreferInfo info = dishesInfo.preferList.get(i);
				CheckBox ckBox = ckList.get(i);
				info.isChecked = ckBox.isChecked();
			}
			dishesInfo.otherPrefer = otherPrefer.getText().toString();
		}
			break;
			
		case R.id.btn_cancel:
			
			break;

		default:
			break;
		}
		
		handler.sendEmptyMessage(0);
	}

}
