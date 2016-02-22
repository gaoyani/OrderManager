package com.huiwei.ordermanager.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.huiwei.ordermanager.R;
import com.huiwei.ordermanager.common.SysApplication;
import com.huiwei.ordermanager.constant.CommonConstant;
import com.huiwei.ordermanager.info.OrderInfo;
import com.huiwei.ordermanager.info.TableInfo;

public class OrderUpdateView extends LinearLayout implements OnClickListener {

	private Context mContext;
	
	private CheckBox addOrder, vipOrder;
	private Spinner tables;
	private EditText notes, peopleNumber;
	private Button ok, cancel;
	
	private List<TableInfo> tableList;
	private Handler handler;
	
	public OrderUpdateView(Context context) {
		super(context);
		mContext = context;
	}
	
	public OrderUpdateView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}
	
	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();
		
		addOrder = (CheckBox) findViewById(R.id.cb_add_order);
		vipOrder = (CheckBox) findViewById(R.id.cb_vip_order);
		tables = (Spinner) findViewById(R.id.spinner_tables);
		notes = (EditText) findViewById(R.id.et_notes);
		peopleNumber = (EditText) findViewById(R.id.et_people_num);
		ok = (Button) findViewById(R.id.btn_ok);
		cancel = (Button) findViewById(R.id.btn_cancel);
		
		if (ok != null) {
			ok.setOnClickListener(this);
			cancel.setOnClickListener(this);
		}
	}
	
	public void setData(List<TableInfo> tableList, Handler handler) {
		this.tableList = tableList;
		this.handler = handler;
		
		addOrder.setChecked(SysApplication.curOrderInfo.isAddOrder);
		vipOrder.setChecked(SysApplication.curOrderInfo.isVip);
		notes.setText(SysApplication.curOrderInfo.notes);
		notes.setSelection(SysApplication.curOrderInfo.notes.length());
		
		List<String> talbesName = new ArrayList<String>();
		int curSelIndex = 0;
		for (int i=0; i<tableList.size(); i++) {
			TableInfo tableInfo = tableList.get(i);
			if (tableInfo.id.equals(SysApplication.curOrderInfo.tableId))
				curSelIndex = i;
			talbesName.add(tableInfo.name);
		}
		
		ArrayAdapter adapter = new ArrayAdapter(mContext, android.R.layout.simple_spinner_item, talbesName.toArray());
		tables.setAdapter(adapter);
		tables.setSelection(curSelIndex);
		tables.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				updatePeopleNum();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});
		
		addOrder.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					updatePeopleNum();
				}
			}
		});
		
		//waimai
		if (SysApplication.curOrderInfo.tableId.equals("-1")) {
			tables.setEnabled(false);
			addOrder.setVisibility(View.GONE);
			vipOrder.setVisibility(View.GONE);
			((LinearLayout)findViewById(R.id.layout_people_num)).setVisibility(View.GONE);
		} 
	}
	
	private void updatePeopleNum() {
		int tableSelIndex = (int) tables.getSelectedItemId();
		int peopleNum = SysApplication.searchPeopleNum(tableList.get(tableSelIndex).id);
		peopleNumber.setText(String.valueOf(peopleNum));
		peopleNumber.setSelection(peopleNumber.getText().length());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_ok: {
			if(Integer.parseInt(peopleNumber.getText().toString()) == 0) {
				Toast.makeText(mContext, R.string.people_number_zero, Toast.LENGTH_SHORT).show();
			} else {
				SysApplication.curOrderInfo.isAddOrder = addOrder.isChecked();
				SysApplication.curOrderInfo.isVip = vipOrder.isChecked();
				
				int tableSelIndex = (int) tables.getSelectedItemId();
				SysApplication.curOrderInfo.tableId = tableList.get(tableSelIndex).id;
				SysApplication.curOrderInfo.tableName = tableList.get(tableSelIndex).name;
				SysApplication.curOrderInfo.notes = notes.getText().toString();
				SysApplication.curOrderInfo.peopleNum = Integer.parseInt(peopleNumber.getText().toString());
				handler.sendEmptyMessage(CommonConstant.OK);
			}
		}
			break;
			
		case R.id.btn_cancel:
			handler.sendEmptyMessage(CommonConstant.CANCEL);
			break;

		default:
			break;
		}
	}

}
