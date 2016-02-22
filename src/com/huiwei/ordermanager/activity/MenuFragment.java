/*****************************************************
 * Copyright(c)2014-2015 ������Ϊ���˿Ƽ����޹�˾
 * MenuFragment.java
 * �����ˣ�������
 * ��     �ڣ�2014-6-20
 * ��     �����˵�ҳ����ʾ�������ļ�
 * ��     ����v6.0
 *****************************************************/
package com.huiwei.ordermanager.activity;

import java.math.BigDecimal;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huiwei.ordermanager.R;
import com.huiwei.ordermanager.adapter.AddDishesItemAdapter;
import com.huiwei.ordermanager.adapter.CategoryItemAdapter;
import com.huiwei.ordermanager.adapter.DishesBaseAdapter;
import com.huiwei.ordermanager.adapter.DishesItemAdapter;
import com.huiwei.ordermanager.baseclass.BaseFragment;
import com.huiwei.ordermanager.common.SysApplication;
import com.huiwei.ordermanager.constant.CommonConstant;
import com.huiwei.ordermanager.constant.UrlConstant;
import com.huiwei.ordermanager.info.DishesInfo;
import com.huiwei.ordermanager.info.OrderInfo;
import com.huiwei.ordermanager.info.OrderedDishesInfo;
import com.huiwei.ordermanager.info.PreferInfo;
import com.huiwei.ordermanager.task.CategoryTask;
import com.huiwei.ordermanager.task.SoldOutDishesTask;
import com.huiwei.ordermanager.task.SyncMenuTask;
import com.huiwei.ordermanager.view.InputDishesView;
import com.huiwei.ordermanager.view.LoadingProgressView;

public class MenuFragment extends BaseFragment implements OnClickListener {
	
	private ListView listViewCategory;
	private ListView listViewDishes;
	private TextView dishesNum, dishesPrice;
	private EditText search;
	private Button cancleSearch, clearSearch;
	private ImageButton startSearch;
	private RelativeLayout layoutSeartch, layoutBottom;
	private LoadingProgressView pbView;
	private DishesBaseAdapter adapter;
	private CategoryItemAdapter categoryAdapter;
	private AlertDialog inputDialog;
	private InputDishesView inputDishesView;

	private boolean isShowMenu = true;
	private int totalNum = 0;
	private int selGroupId = 0;
	private OrderInfo tempOrderInfo = new OrderInfo();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_menu, container, false);
		dishesNum = (TextView)view.findViewById(R.id.tv_dishes_num);
		dishesPrice = (TextView)view.findViewById(R.id.tv_price);
		pbView = (LoadingProgressView)view.findViewById(R.id.loading_view);
		pbView.setVisibility(View.GONE);
		cancleSearch = (Button)view.findViewById(R.id.btn_cancle_search);
		cancleSearch.setOnClickListener(this);
		clearSearch = (Button)view.findViewById(R.id.btn_clean_search);
		clearSearch.setOnClickListener(this);
		startSearch = (ImageButton)view.findViewById(R.id.btn_search);
		startSearch.setOnClickListener(this);
		layoutSeartch = (RelativeLayout)view.findViewById(R.id.layout_search);
		layoutBottom = (RelativeLayout)view.findViewById(R.id.layout_bottom);
		layoutBottom.setOnClickListener(this);
		layoutBottom.setVisibility(isShowMenu ? view.GONE : view.VISIBLE);
		
		initButtons(view);
		
		dishesNum.setText(String.valueOf(totalNum)+
				getResources().getString(R.string.fen));

		initCategoryListView(view);
		initDishesListView(view);
		initSearchText(view);
		
		return view;
	}
	
	/*****************************************************
	 * ������initButtons
	 * ��     �룺View view -- ҳ�����ʵ��
	 * ��     ������
	 * ��     ������ʼ����������ť
	 * ���ýӿڣ�onCreateView
	 * �����ˣ�������
	 * ��     �ڣ�2014-6-20
	 *****************************************************/
	private void initButtons(View view) {
		ImageView returnBtn = (ImageView)view.findViewById(R.id.iv_return);
		returnBtn.setVisibility(isShowMenu ? View.GONE : View.VISIBLE);
		returnBtn.setOnClickListener(this);
		
		ImageView menuBtn = (ImageView)view.findViewById(R.id.iv_menu);
		menuBtn.setVisibility(isShowMenu ? View.VISIBLE : View.GONE);
		menuBtn.setOnClickListener(this);
		
		ImageView inputBtn = (ImageView)view.findViewById(R.id.iv_input_dishes);
		inputBtn.setVisibility(isShowMenu ? View.GONE : View.VISIBLE);
		inputBtn.setOnClickListener(this);
	}
	
	public void setFlag(boolean isShowMenu) {
		this.isShowMenu = isShowMenu;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void onResume() {
		refreshMenu();
		super.onResume();
	}
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		if (!hidden) {
			refreshMenu();
		}
		
		Log.d("MenuFragment", "onHiddenChanged");
		Log.d("hidden", hidden ? "true" : "false");
		super.onHiddenChanged(hidden);
	}
	
	/*****************************************************
	 * ������refreshMenu
	 * ��     �룺��
	 * ��     ������
	 * ��     ����ˢ�²˵��б�
	 * ���ýӿڣ�onResume
	 * �����ˣ�������
	 * ��     �ڣ�2014-6-20
	 *****************************************************/
	private void refreshMenu() {
//		SysApplication.searchDishesIDList.clear();
//		adapter.notifyDataSetChanged();
//		adapter.setData(SysApplication.dishesList, SysApplication.searchDishesIDList);
		
		selGroupId = 0;
		categoryAdapter.setData(SysApplication.categoryList);
		syncMenu();
		
//		if (SysApplication.dishesList.size() == 0) {
//			syncMenu();
//		} else {
//			selGroupId = 0;
//			updateGroupDishes();
//			categoryAdapter.setData(SysApplication.categoryList);
//			pbView.setVisibility(View.GONE);
//		}
	}
	
	/*****************************************************
	 * ������syncMenu
	 * ��     �룺��
	 * ��     ������
	 * ��     ����ͬ���˵�
	 * ���ýӿڣ�refreshMenu
	 * �����ˣ�������
	 * ��     �ڣ�2014-6-20
	 *****************************************************/
	private void syncMenu() {
//		CategoryTask ct = new CategoryTask(getActivity(), handleCategory);
//		ct.execute(UrlConstant.getServerUrl(getActivity()));
//		
//		SyncMenuTask smt = new SyncMenuTask(getActivity(), handlerSyncMenu);
//		smt.execute(UrlConstant.getServerUrl(getActivity()));
		
		SoldOutDishesTask task = new SoldOutDishesTask(getActivity(), handlerSyncMenu);
		task.execute(UrlConstant.getServerUrl(getActivity()));
		
//		pbView.showProgressBar();
//		pbView.setHandler(handlerSyncMenu);
	}
	
	Handler handleCategory = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			categoryAdapter.setData(SysApplication.categoryList);
		}
	};
	
	//ͬ���˵��߳���Ϣ����
	Handler handlerSyncMenu = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
//			if (msg.what == LoadingProgressView.RELOADING) {
//				syncMenu();
//				return;
//			}
			
			pbView.hideView();
			if (msg.what == CommonConstant.SUCCESS) {
				updateGroupDishes();
			} else {
//				pbView.showLoadingFailed();
			}
		}
	};

	@Override
	public void onDestroyView() {
		super.onDestroy();
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
	}
	
	/*****************************************************
	 * ������initCategoryListView
	 * ��     �룺View view -- ҳ�����ʵ��
	 * ��     ������
	 * ��     ������ʼ�������б�
	 * ���ýӿڣ�onCreateView
	 * �����ˣ�������
	 * ��     �ڣ�2014-6-20
	 *****************************************************/
	private void initCategoryListView(View view) {
		listViewCategory = (ListView)view.findViewById(R.id.lv_catagery);
		categoryAdapter = new CategoryItemAdapter(
				getActivity().getApplicationContext());
		listViewCategory.setAdapter(categoryAdapter);
		listViewCategory.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				categoryAdapter.setSelectItemID(position);
				categoryAdapter.setData(SysApplication.categoryList);
				
				selGroupId = SysApplication.categoryList.get(position).id;
				updateGroupDishes();
			}
		});
	}
	
	private void updateGroupDishes() {
		SysApplication.searchDishesGroup(selGroupId);
//		adapter.notifyDataSetChanged();
		adapter.setData(SysApplication.dishesList, SysApplication.searchDishesIDList);
	}
	
	/*****************************************************
	 * ������initDishesListView
	 * ��     �룺View view -- ҳ�����ʵ��
	 * ��     ������
	 * ��     ������ʼ���˵��б�
	 * ���ýӿڣ�onCreateView
	 * �����ˣ�������
	 * ��     �ڣ�2014-6-20
	 *****************************************************/
	private void initDishesListView(View view) {
		listViewDishes = (ListView)view.findViewById(R.id.lv_dishes);
		listViewDishes.setSelector(R.color.transparent);
		if (isShowMenu) {
			adapter = new DishesItemAdapter(getActivity().
					getApplicationContext(), handler);
		} else {
			adapter = new AddDishesItemAdapter(getActivity().
					getApplicationContext(), handler, tempOrderInfo);
		}
		
		adapter.setData(SysApplication.dishesList, SysApplication.searchDishesIDList);
		listViewDishes.setAdapter(adapter);
	} 
	
	/*****************************************************
	 * ������initSearchText
	 * ��     �룺View view -- ҳ�����ʵ��
	 * ��     ������
	 * ��     ������ʼ���������ܣ����������ı�����
	 * ���ýӿڣ�onCreateView
	 * �����ˣ�������
	 * ��     �ڣ�2014-6-20
	 *****************************************************/
	private void initSearchText(View view) {
		search = (EditText)view.findViewById(R.id.et_search);
		search.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.toString().length() == 0) {
					clearSearch.setVisibility(View.GONE);
					SysApplication.clearSearch();
				} else {
					clearSearch.setVisibility(View.VISIBLE);
					SysApplication.searchDishesName(s.toString().toUpperCase());
				}
				
//				adapter.notifyDataSetChanged();
				adapter.setData(SysApplication.dishesList, SysApplication.searchDishesIDList);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
	}
	
	//�˵��б��е���������Ϣ����
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			int num = msg.arg1;			
			
			String dishesId = (String) msg.obj;
			OrderedDishesInfo info = tempOrderInfo.findOrderedDishes(dishesId);
			if (msg.what == 1) {
				if (info == null) {
					info = new OrderedDishesInfo();
					info.orderNum = String.valueOf(num);
					DishesInfo dishesInfo = SysApplication.dishesList.get(dishesId);
					info.dishes.id = dishesInfo.id;
					info.dishes.name = dishesInfo.name;
					info.dishes.price = dishesInfo.price;
					info.dishes.vipPrice = dishesInfo.vipPrice;
					info.dishes.islimit = dishesInfo.islimit;
		
					for (PreferInfo preferInfoTmp : dishesInfo.preferList) {
						PreferInfo preferInfo = new PreferInfo();
						preferInfo.id = preferInfoTmp.id;
						preferInfo.name = SysApplication.preferList.get(preferInfo.id).name;
						info.dishes.preferList.add(preferInfo);
					}
					
					tempOrderInfo.dishesInfo.add(info);
				} else {
					info.orderNum = String.valueOf(num);
				}
			} else {
				if (info != null && info.orderNum.equals("1")) {
					tempOrderInfo.dishesInfo.remove(info);
				} else {
					info.orderNum = String.valueOf(num);
				}
			}	
			
			updatePrice();
		};
	};
	
	/*****************************************************
	 * ������updatePrice
	 * ��     �룺��
	 * ��     ������
	 * ��     ����ˢ���ѵ�˵����ܼ۸�
	 * �����ˣ�������
	 * ��     �ڣ�2014-6-20
	 *****************************************************/
	private void updatePrice() {
		float total = 0;
		totalNum = 0;
		for (OrderedDishesInfo info : tempOrderInfo.dishesInfo) {
			float price = Float.parseFloat(info.dishes.price)*
					Integer.parseInt(info.orderNum);
			
			total += price;
			totalNum += Integer.valueOf(info.orderNum);
		}

		float result = new BigDecimal(total).setScale(2, 
				BigDecimal.ROUND_HALF_UP).floatValue(); 
		
		dishesNum.setText(String.valueOf(totalNum)+
				getResources().getString(R.string.fen));
		dishesPrice.setText(getResources().getString(R.string.yuan)+
				String.valueOf(result));
	}

	//��ť�ĵ����Ϣ����
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_return:
			returnBack();
			break;
			
		case R.id.iv_menu:
			listener.onMenuPop();
			break;
			
		case R.id.iv_input_dishes:  
			popInputDialog();
			break;
			
		case R.id.layout_bottom:
			updateOrderInfo();
			break;
			
		case R.id.btn_cancle_search:
			search.setText("");
			layoutSeartch.setVisibility(View.GONE);
			startSearch.setVisibility(View.VISIBLE);
			listViewCategory.setVisibility(View.VISIBLE);
			SysApplication.clearSearch();
			break;
			
		case R.id.btn_clean_search:
			search.setText("");
			break;
			
		case R.id.btn_search:
			layoutSeartch.setVisibility(View.VISIBLE);
			startSearch.setVisibility(View.GONE);
			SysApplication.clearSearch();
			categoryAdapter.setSelectItemID(0);
			categoryAdapter.setData(SysApplication.categoryList);
			listViewCategory.setVisibility(View.GONE);
			break;
			
		default:
			break;
		}
	}
	
	/*****************************************************
	 * ������popInputDialog
	 * ��     �룺��
	 * ��     ������
	 * ��     ����������д�˵�����Ի���
	 * �����ˣ�������
	 * ��     �ڣ�2014-6-20
	 *****************************************************/
	private void popInputDialog() {
		if (inputDialog == null) {
			inputDialog = new AlertDialog.Builder(getActivity()).create();
			LayoutInflater inflater = LayoutInflater.from(getActivity());
			inputDishesView = (InputDishesView) 
					inflater.inflate(R.layout.input_dishes_view, null);
		}
		
		inputDishesView.setData(tempOrderInfo, inputHandler);
		inputDialog.setView(inputDishesView, 0, 0, 0, 0);
		inputDialog.show();
	}

	private Handler inputHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			inputDialog.dismiss();
			updatePrice();
			totalNum = tempOrderInfo.dishesInfo.size();
			dishesNum.setText(String.valueOf(totalNum)+
					getResources().getString(R.string.fen));
		}
	};

	//���ش���
	public void returnBack() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setMessage(R.string.cancle_add_title);
		builder.setNegativeButton(R.string.cancle_add_ok, 
				new DialogInterface.OnClickListener() {
	
			@Override
			public void onClick(DialogInterface dialog, int which) {
				getActivity().finish();
			}
		});
		builder.setPositiveButton(R.string.cancle_add_no,
				new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		builder.create();
		builder.show();
	}
	
	/*****************************************************
	 * ������updateOrderInfo
	 * ��     �룺��
	 * ��     ������
	 * ��     �������¶�����Ϣ
	 * �����ˣ�������
	 * ��     �ڣ�2014-6-20
	 *****************************************************/
	private void updateOrderInfo() {
		for (OrderedDishesInfo info : tempOrderInfo.dishesInfo) {
			if (info.isInput) {
				SysApplication.curOrderInfo.dishesInfo.add(info);
			} else {
				OrderedDishesInfo dishesInfo = SysApplication.
						curOrderInfo.findOrderedDishes(info.dishes.id);
				
				if (dishesInfo == null) {
					SysApplication.curOrderInfo.dishesInfo.add(info);
				} else {
					dishesInfo.orderNum = info.orderNum;
				}
			}
		}
		
		getActivity().finish();
	}
}
