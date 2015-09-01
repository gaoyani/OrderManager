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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huiwei.ordermanager.R;
import com.huiwei.ordermanager.adapter.InputDishesItemAdapter;
import com.huiwei.ordermanager.info.DishesInfo;
import com.huiwei.ordermanager.info.OrderInfo;
import com.huiwei.ordermanager.info.OrderedDishesInfo;
import com.huiwei.ordermanager.info.PreferInfo;

public class InputDishesView extends RelativeLayout implements OnClickListener {

	private Context mContext;
	
//	private List<OrderedDishesInfo> tempList = new ArrayList<OrderedDishesInfo>();
	private EditText name, price;
//	private ListView dishesDisplay;
//	private Button add;
	private Button ok, cancel;
	
//	private InputDishesItemAdapter adapter;
	private OrderInfo orderInfo;
	private Handler handler;
	
	public InputDishesView(Context context) {
		super(context);
		mContext = context;
	}
	
	public InputDishesView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}
	
	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();
		
		name = (EditText) findViewById(R.id.et_dishes_name);
		price = (EditText) findViewById(R.id.et_dishes_price);
//		dishesDisplay = (ListView) findViewById(R.id.lv_dishes);
	
		ok = (Button) findViewById(R.id.btn_ok);
		cancel = (Button) findViewById(R.id.btn_cancel);
//		add = (Button) findViewById(R.id.btn_input_add);
		if (ok != null) {
			ok.setOnClickListener(this);
			cancel.setOnClickListener(this);
//			add.setOnClickListener(this);
		}
		
//		if (dishesDisplay != null) {
//			adapter = new InputDishesItemAdapter(mContext, tempList);
//			dishesDisplay.setAdapter(adapter);
//		}
	}
	
	public void setData(OrderInfo orderInfo, Handler handler) {
		this.handler = handler;
		this.orderInfo = orderInfo;
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
//		case R.id.btn_input_add: {
//			if (checkInput()) {
//				OrderedDishesInfo info = new OrderedDishesInfo();
//				info.dishes.name = name.getText().toString();
//				info.dishes.price = price.getText().toString();
//				info.isInput = true;
//				tempList.add(info);
//				adapter.notifyDataSetChanged();
//				name.setText("");
//				price.setText("");
//			}
//		}
//			break;
			
		case R.id.btn_ok: {
			if (checkInput()) {
				OrderedDishesInfo info = new OrderedDishesInfo();
				info.dishes.id = "0";
				info.dishes.name = name.getText().toString();
				info.dishes.price = price.getText().toString();
				info.isInput = true;
				orderInfo.dishesInfo.add(info);
				handler.sendEmptyMessage(0);
			}
		}
			break;
			
		case R.id.btn_cancel:
			handler.sendEmptyMessage(0);
			break;

		default:
			break;
		}
		
		
	}
	
	private boolean checkInput() {
		if (name.getText().toString().length() == 0) {
			Toast.makeText(mContext, mContext.getResources().getString(
					R.string.input_dishes_name_empty), Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if (price.getText().toString().length() == 0) {
			Toast.makeText(mContext, mContext.getResources().getString(
					R.string.input_dishes_price_empty), Toast.LENGTH_SHORT).show();
			return false;
		}

		try {
			Double.valueOf(price.getText().toString());
		} catch (NumberFormatException e) {
			Toast.makeText(mContext, mContext.getResources().getString(
					R.string.input_dishes_price_error), Toast.LENGTH_SHORT).show();
			return false;
		}
		
		return true;
	}

}
