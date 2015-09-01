package com.huiwei.ordermanager.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.huiwei.ordermanager.R;

public class NumberView extends RelativeLayout implements OnClickListener {

	private Context mContext;
	
	private int num;
	private String dishesId;
	
	private EditText numberEdit;
	private ImageButton add;
	private ImageButton subtract;
	
	private Handler handler;
	
	public NumberView(Context context) {
		super(context);
		mContext = context;
	}
	
	public NumberView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}
	
	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();
		
		numberEdit = (EditText) findViewById(R.id.et_num);
		add = (ImageButton)findViewById(R.id.ib_add);
		subtract = (ImageButton) findViewById(R.id.ib_subtract);
		if (add != null) {
			add.setOnClickListener(this);
			subtract.setOnClickListener(this);
			numberEdit.setTextColor(Color.BLACK);
		}
	}
	
	public void setData(String num, String id, Handler handler) {
		this.num = Integer.parseInt(num);
		numberEdit.setText(num);
		if (Integer.valueOf(num) > 1) {
			numberEdit.setTextColor(Color.RED);
		} else {
			numberEdit.setTextColor(Color.BLACK);
		}
		
		this.dishesId = id;
		this.handler = handler;
	}
	
	public String getNum() {
		return numberEdit.getText().toString();
	}
	
	public void setEnable(boolean isEnable) {
		add.setEnabled(isEnable);
		numberEdit.setEnabled(isEnable);
		numberEdit.setTextColor(isEnable ? Color.BLACK : Color.GRAY);
		subtract.setEnabled(isEnable);
	}

	@Override
	public void onClick(View v) {
		Message msg = new Message();
		msg.arg1 = 0;
		
		switch (v.getId()) {
		case R.id.ib_add:
			num += 1;
			msg.arg1 = 1;
			break;
			
		case R.id.ib_subtract:
			if (num > 0) {
				num -= 1;
				msg.arg1 = -1;
			}
			break;

		default:
			break;
		}
		
		numberEdit.setText(String.valueOf(num));
		if (num > 1) {
			numberEdit.setTextColor(Color.RED);
		} else {
			numberEdit.setTextColor(Color.BLACK);
		}
		
		
		msg.obj = dishesId;
		handler.sendMessage(msg);
	}
}
