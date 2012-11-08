package com.swm.vg.voiceprotoapp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.swm.vg.data.AnimalInfo;
import com.swm.vg.data.RecognizedData;
import com.swm.vg.voicetoactions.VoiceRecognizer;
import com.swm.vg.voicetoactions.VoiceRecognizerListener;

public class TeachActivity extends Activity {
	private VoiceRecognizer mRecognizer;
	
	private final static int MAX_VOICERECOG = 5;
	
	private final static int DLG_RECOGNITION = 1;
	
	private ListeningDialog mRecogDlg = null;
	
	private Button btnTeach;
	private Spinner spinAnimals, spinActions;
	private ArrayAdapter<String> adapterAnimals = null;
	private ArrayAdapter<CharSequence> adapterActions = null;
	
	private final RecognizedData mData;
	private final ArrayList<AnimalInfo> animalList;
	private ArrayList<String> animalNames;
	
	public TeachActivity() {
		mData = RecognizedData.sharedRecognizedData();
		animalList = mData.getAnimalList();
		animalNames = mData.getAnimalIDsAndNames();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_teach);
		
		mRecognizer = VoiceRecognizer.createVoiceRecognizer(this, false);
		mRecognizer.setVoiceRecognizerListener(new MyVoiceRecognizerListener());
		
		btnTeach = (Button)findViewById(R.id.teach_btn_teachStart);
		spinAnimals = (Spinner)findViewById(R.id.teach_spin_animal);
		spinActions = (Spinner)findViewById(R.id.teach_spin_action);
		
		mRecogDlg = new ListeningDialog(this);
		
		spinAnimals.setPrompt("Select Animal");
		adapterAnimals = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, animalNames);
		adapterAnimals.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinAnimals.setAdapter(adapterAnimals);
		spinAnimals.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {}
			public void onNothingSelected(AdapterView<?> arg0) {
				new AlertDialog.Builder(TeachActivity.this).setTitle("경고")
					.setMessage("동물을 선택해주세요!")
					.setPositiveButton("확인", null)
					.show();
			}
			
		});
		
		spinActions.setPrompt("Select Action");
		adapterActions = ArrayAdapter.createFromResource(this,
				R.array.actionList_strings, android.R.layout.simple_spinner_item);
		adapterActions.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinActions.setAdapter(adapterActions);
		spinActions.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {}
			public void onNothingSelected(AdapterView<?> arg0) {
				new AlertDialog.Builder(TeachActivity.this).setTitle("경고")
					.setMessage("액션을 선택해주세요!")
					.setPositiveButton("확인", null)
					.show();
			}
			
		});
		
		btnTeach.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String animalInfo = (String)spinAnimals.getSelectedItem();
				if(animalInfo == null || animalInfo.equals("") ||
						animalInfo.equals(" ")) {
					new AlertDialog.Builder(TeachActivity.this).setTitle("경고")
					.setMessage("동물을 선택해주세요!")
					.setPositiveButton("확인", null)
					.show();
					return;
				}
				String actionInfo = (String)spinActions.getSelectedItem();
				if(actionInfo == null || actionInfo.equals("") ||
						actionInfo.equals(" ")) {
					new AlertDialog.Builder(TeachActivity.this).setTitle("경고")
					.setMessage("액션을 선택해주세요!")
					.setPositiveButton("확인", null)
					.show();
					return;
				}
				mRecognizer.start();
				showDialog(DLG_RECOGNITION);
			}
		});
	};
	
	private class MyVoiceRecognizerListener implements VoiceRecognizerListener {
		
		@Override
		public void onVolumeChanged(int step) {}
		
		@Override
		public void onTimeoutRecognition() {}
		
		@Override
		public void onStartRecognition() {}
		
		@Override
		public void onResults(final ArrayList<String> results) {
			new AlertDialog.Builder(TeachActivity.this).setTitle("가르치기 음성 인식 완료")
				.setMessage("인식된 음성으로 액션 명령을 가르치시겠습니까?")
				.setPositiveButton("예", new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						mRecogDlg.setText("saving...");
						
						StringTokenizer token = new StringTokenizer((String)spinAnimals.getSelectedItem(), ":");
						final int animalId = Integer.parseInt(token.nextToken());
						StringTokenizer token2 = new StringTokenizer((String)spinActions.getSelectedItem(), ":");
						final int actionId = Integer.parseInt(token2.nextToken());
						
						mData.getAnimalInfo(animalId).addActionVoice(actionId, results);
						
						Toast.makeText(TeachActivity.this, "저장 완료", Toast.LENGTH_SHORT).show();
						dismissDialog(DLG_RECOGNITION);
					}
				}).setNegativeButton("아니요", new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dismissDialog(DLG_RECOGNITION);
					}
				}).show();
		}
		
		@Override
		public void onReady() {
			mRecogDlg.setText("recognition ready");
		}
		
		@Override
		public void onFinishRecognition() {}
		
		@Override
		public void onError(int error) {
			Toast.makeText(TeachActivity.this, "Error Code : " + error, Toast.LENGTH_SHORT).show();
			dismissDialog(DLG_RECOGNITION);
		}
		
		@Override
		public void onEndOfSpeech() {
			mRecogDlg.setStoppable(false);
			mRecogDlg.setText("analyzing...");	
		}
		
		@Override
		public void onClosing() {}
		
		@Override
		public void onCancelRecognition() {
			dismissDialog(DLG_RECOGNITION);
		}
		
		@Override
		public void onBeginningOfSppech() {
			mRecogDlg.setText("recognizing...");
		}
	};
	
	@Override
	protected void onDestroy() {
		mRecognizer.closeVoiceRecognizer();
		super.onDestroy();
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id) {
		case DLG_RECOGNITION:
			return mRecogDlg;
		default:
			return super.onCreateDialog(id);
		}
	}
	
	protected void onPrepareDialog(int id, android.app.Dialog dialog) {
		switch(id) {
		case DLG_RECOGNITION:
			mRecogDlg.setText("prepare...");
			mRecogDlg.prepare(new View.OnClickListener() {
				public void onClick(View v) {
					mRecognizer.cancel();
					dismissDialog(DLG_RECOGNITION);
				}
			});
			break;
		default:
			break;
		}
	}
}
