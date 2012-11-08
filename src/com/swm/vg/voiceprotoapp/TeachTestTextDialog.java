package com.swm.vg.voiceprotoapp;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class TeachTestTextDialog extends Dialog {
	EditText edtText = null;
	Button btnTeach = null;
	
	private TeachTestTextDialog(Activity owner) {
        super(owner);
      
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dlg_teach_test_text);
        setOwnerActivity(owner);
        
        WindowManager.LayoutParams layout = getWindow().getAttributes();
        layout.width = WindowManager.LayoutParams.FILL_PARENT;

        edtText = (EditText)findViewById(R.id.dlgTeachTest_edt_content);
        btnTeach = (Button)findViewById(R.id.dlgTeachTest_btn_teach);
    }
    
    public TeachTestTextDialog(Activity owner, View.OnClickListener l) {
    	this(owner);
    	
    	btnTeach.setOnClickListener(l);
    }
    
    public void resetEditText() {
    	edtText.setText("");
    }
    
    public String getText() {
    	return edtText.getText().toString();
    }
    
    public void setSayTestDialog() {
    	btnTeach.setText("말하기");
    	((TextView)findViewById(R.id.dlgTeachTest_title)).setText("말하기 테스트 텍스트");
    }
}
