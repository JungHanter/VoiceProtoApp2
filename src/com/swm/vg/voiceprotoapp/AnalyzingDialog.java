package com.swm.vg.voiceprotoapp;

import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class AnalyzingDialog extends Dialog {
    private String _text;
    private boolean _analyzing;
    
    public AnalyzingDialog(Activity owner) 
    {
        super(owner);
        
        _text = null;
        _analyzing = false;
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dlg_analyzing);
        setOwnerActivity(owner);
        setCancelable(false); 

        WindowManager.LayoutParams layout = getWindow().getAttributes();
        layout.gravity = Gravity.BOTTOM;
        layout.width = WindowManager.LayoutParams.FILL_PARENT;
        
        ((Button)findViewById(R.id.dlgAnal_btn_analyzingStop)).setEnabled(false);
    }
    
    public void setText(String text)
    {
        _text = text;
        TextView t = (TextView)findViewById(R.id.dlgAnal_text_analyzingStatus);
        if (t != null)
        {
            t.setText(text);
        }
    }
    
    public void setAnalyzing(boolean analyzing)
    {
    	_analyzing = analyzing;
    }
    
    public String getText()
    {
        return _text;
    }
    public boolean isAnalyzing()
    {
        return _analyzing;
    }

    public void prepare()
    {
        if (_text != null)
        {
            setText(_text);
        }
        _analyzing = true;
    }
}