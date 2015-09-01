package com.huiwei.ordermanager.baseclass;

import com.huiwei.ordermanager.R;
import com.huiwei.ordermanager.activity.MainActivity;
import com.huiwei.ordermanager.activity.UserLoginActivity;
import com.huiwei.ordermanager.common.SysApplication;
import com.huiwei.ordermanager.constant.CommonConstant;
import com.huiwei.ordermanager.constant.UrlConstant;
import com.huiwei.ordermanager.task.LoginTask;
import com.huiwei.ordermanager.task.LogoutTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

public class BaseActivity extends FragmentActivity
{
	protected LoadDialogDao load = null;
	protected Context mContext = null;
	protected String mStrMsg = null;
	
    protected BaseActivity()
    {
    	SetInstance(this);
    }
	
    protected Handler baseHandler = new Handler()
    {
    	@Override
		public void handleMessage(Message msg)
		{   		
    		switch(msg.what)
    		{
	    		case CommonConstant.DATA_LOADING_START_STATUS:
	    			mStrMsg = msg.getData().getString(CommonConstant.DATA_LOADING_START_NAME);
	    			load = new LoadDialogDao(GetInstance(), mStrMsg);
	    	    	load.show();
	    			break;
	    		case CommonConstant.DATA_LOADING_END_STATUS:
	    			if(load != null)
	    			{
	    				load.hide();
	    			}
	    			break;
	    		default:
	    			break;
    		}
		}
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState){
    	super.onCreate(savedInstanceState);
    }
    
    @Override
    protected void onResume() {
    	
    	super.onResume();
    }
    
    @Override
    protected void onPause() {
    	
    	super.onPause();
    }

    @Override
    protected void onDestroy()
    {
    	super.onDestroy();
    }
    
    protected Context GetInstance()
    {
    	return mContext;
    }

    protected void SetInstance(Context context)
    {
    	mContext = context;
    }
}
