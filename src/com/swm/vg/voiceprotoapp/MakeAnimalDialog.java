package com.swm.vg.voiceprotoapp;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MakeAnimalDialog extends Dialog {
	EditText edtName = null;
	Button btnMake = null;
	
	private MakeAnimalDialog(Activity owner) {
        super(owner);
      
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dlg_make_animal);
        setOwnerActivity(owner);
        
        WindowManager.LayoutParams layout = getWindow().getAttributes();
        layout.width = WindowManager.LayoutParams.FILL_PARENT;

        edtName = (EditText)findViewById(R.id.dlgMakeAnimal_edt_name);
        btnMake = (Button)findViewById(R.id.dlgMakeAnimal_btn_make);
    }
    
    public MakeAnimalDialog(Activity owner, View.OnClickListener l) {
    	this(owner);
    	
    	btnMake.setOnClickListener(l);
    }
    
    public void resetEditText() {
    	edtName.setText("");
    }
    
    public String getName() {
    	return edtName.getText().toString();
    }
}
