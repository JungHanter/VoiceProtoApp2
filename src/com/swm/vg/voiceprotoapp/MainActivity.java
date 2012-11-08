package com.swm.vg.voiceprotoapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.swm.vg.data.RecognizedData;

public class MainActivity extends Activity {
	static PowerManager.WakeLock mWakeLock;
	
	private final static int WAITING_DIALOG = 0;
	private WaitingDialog waitingDlg;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        waitingDlg = new WaitingDialog(this);
        
        PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "VoiceRecog");
        
        showDialog(WAITING_DIALOG);
        
        RecognizedData data = RecognizedData.sharedRecognizedData();
        data.loadAnimalList();
        
//        test();
        Log.i("VoiceProtoApp Init", "Data Load Done.");
        
        dismissDialog(WAITING_DIALOG);
    }
    
    private void test() {
    	String result = "";
    	
    	String str = "뽀삐야 밥먹어";
    	String[] a = str.split("뽀삐");
    	result += ("" + a.length +"개\n");
    	for(String s : a)
    		result += (s + '/');
    	

    	
    	Log.d("test", result);
    	Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
    }

    public void onButtonClick(View v) {
    	switch(v.getId()) {
    	case R.id.main_btn_teachName:
    		startActivity(new Intent(MainActivity.this, TeachNameActivity.class));
    		break;
    		
    	case R.id.main_btn_teach:
    		startActivity(new Intent(MainActivity.this, TeachActivity.class));
    		break;
    		
    	case R.id.main_btn_communicate:
    		startActivity(new Intent(MainActivity.this, CommunicateActivity.class));
    		break;
    		
    	case R.id.main_btn_patternList:
    		startActivity(new Intent(MainActivity.this, PatternMatchListActivity.class));
    		break;
    	}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
    protected void onResume() {
    	//사용확인 후 사용
    	if(!mWakeLock.isHeld()) mWakeLock.acquire();
    	super.onResume();
    }
    
    @Override
    protected void onDestroy() {
    	//사용 중지
    	mWakeLock.release();
    	super.onDestroy();
    }
    
    @Override
	protected void onPrepareDialog(int id, final Dialog dialog) {
		switch (id) {
		case WAITING_DIALOG:
			break;
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case WAITING_DIALOG:
			return waitingDlg;
		}
		return null;
	}
}
