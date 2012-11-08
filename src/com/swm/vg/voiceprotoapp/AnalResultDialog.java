package com.swm.vg.voiceprotoapp;

import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class AnalResultDialog extends Dialog {
	TextView txtTitle=null, txtMorphAnal=null, txtResultTitle=null, txtResultContent=null;
	Button btnClose = null;
    
    private AnalResultDialog(Activity owner) {
        super(owner);
      
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dlg_analyze_result);
        setOwnerActivity(owner);
        
        WindowManager.LayoutParams layout = getWindow().getAttributes();
        layout.width = WindowManager.LayoutParams.FILL_PARENT;

        txtTitle = (TextView)findViewById(R.id.dlgAnalResult_txt_title);
        txtMorphAnal = (TextView)findViewById(R.id.dlgAnalResult_txt_morphAnal);
        txtResultTitle = (TextView)findViewById(R.id.dlgAnalResult_txt_result_title);
        txtResultContent = (TextView)findViewById(R.id.dlgAnalResult_txt_result_content);
        btnClose = (Button)findViewById(R.id.dlgAnalResult_btn_close);
        
        btnClose.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				AnalResultDialog.this.dismiss();
			}
		});
    }
    
    public AnalResultDialog(Activity owner, String title, String resultTitle) {
    	this(owner);
    	
    	txtTitle.setText(title);
    	txtResultTitle.setText(resultTitle);
    }

	public void setMorphAnalText(CharSequence analText) {
		txtMorphAnal.setText(analText);
	}
	
	public void setResultText(CharSequence resultText) {
		txtResultContent.setText(resultText);
	}
}
