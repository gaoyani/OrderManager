/*
 * load¶Ô»°¿ò
 */
package com.huiwei.ordermanager.baseclass;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

public class LoadDialogDao 
{
	private Context mContext = null;
	ProgressDialog load = null;
	
	public LoadDialogDao(Context context,String msg) 
	{
		super();
		mContext = context;
		load = new ProgressDialog(mContext);
		load.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		load.setMessage(msg);
		load.setIndeterminate(false);
		load.setCancelable(true);
	}
	
	public void ChangeDlgMsg(String msg)
	{
		load.setMessage(msg);
	}
	
	public void show()
	{
		load.show();
	}
	
	public void hide()
	{
		load.dismiss();
	}
	
	public void error(String err)
	{
		Toast.makeText(mContext,(CharSequence)err, Toast.LENGTH_LONG).show();
		this.hide();
	}
}
