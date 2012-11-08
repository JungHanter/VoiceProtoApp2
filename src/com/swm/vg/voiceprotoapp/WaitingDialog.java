package com.swm.vg.voiceprotoapp;

import android.app.Activity;
import android.app.Dialog;
import android.view.Window;
import android.widget.TextView;

public class WaitingDialog extends Dialog {
	private TextView txtContent;
	
	public WaitingDialog(Activity owner) {
		super(owner);
	
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dlg_wating);
        setOwnerActivity(owner);
        setCancelable(false); 
        
        txtContent = (TextView)findViewById(R.id.dlgWait_txt_content);
	}
	
	public void setText(String text) {
		txtContent.setText(text);
	}
	
	public String getText() {
		return txtContent.getText().toString();
	}
	
	@Override
	public void onBackPressed() {
		return;
	}
}
