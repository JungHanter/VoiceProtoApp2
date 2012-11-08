package com.swm.vg.voiceprotoapp;

import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

class ListeningDialog extends Dialog
{
    private String _text;
    
    public ListeningDialog(Activity owner) 
    {
        super(owner);
        
        _text = null;
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dlg_listening);
        setOwnerActivity(owner);
        setCancelable(false); 
        
        ((TextView)findViewById(R.id.dlgLsn_text_recordLevel)).setVisibility(View.GONE);
        
        WindowManager.LayoutParams layout = getWindow().getAttributes();
        layout.gravity = Gravity.BOTTOM;
        layout.width = WindowManager.LayoutParams.FILL_PARENT;
    }
    
    public void setText(String text)
    {
        _text = text;
        TextView t = (TextView)findViewById(R.id.dlgLsn_text_listeningStatus);
        if (t != null)
        {
            t.setText(text);
        }
    }
     
    public void setStoppable(boolean stoppable)
    {
        setButtonEnabled((Button)findViewById(R.id.dlgLsn_btn_listeningStop), stoppable);
    }
    
    private void setButtonEnabled(Button b, boolean enabled)
    {
        if (b != null) b.setEnabled(enabled);
    }

    public void prepare(Button.OnClickListener stopButtonListener)
    {
        if (_text != null)
        {
            setText(_text);
        }
        
        Button b = (Button)findViewById(R.id.dlgLsn_btn_listeningStop);
        setButtonEnabled(b, true);
        b.setOnClickListener(stopButtonListener);
    }
    
    @Override
    public void onBackPressed() {
    	return;
    }
}
