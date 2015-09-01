package com.huiwei.ordermanager.baseclass;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class BaseFragment extends Fragment {
	
	protected OnMenuPopListener listener;


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	public void setOnMenuPopListener(OnMenuPopListener listener) {
		this.listener = listener;
	}
	
	public interface OnMenuPopListener { 
		public void onMenuPop(); 
		} 
}




