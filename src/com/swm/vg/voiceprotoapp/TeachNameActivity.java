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

public class TeachNameActivity extends Activity {
	private VoiceRecognizer mRecognizer;
	
	private final static int DLG_MAKE_ANIMAL = 0;
	private final static int DLG_RECOGNITION = 1;
	
	private MakeAnimalDialog mMakeDlg = null;
	private ListeningDialog mRecogDlg = null;
	
	private Button btnMakeAnimal, btnTeachName;
	private Spinner spinAnimals;
	private ArrayAdapter<String> adapter = null;

	private final RecognizedData mData;
	private final ArrayList<AnimalInfo> animalList;
	private ArrayList<String> animalNames;
	
	private final static int MAX_VOICENAME = 3;
	
	public TeachNameActivity() {
		mData = RecognizedData.sharedRecognizedData();
		animalList = mData.getAnimalList();
		animalNames = mData.getAnimalIDsAndNames();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_teach_name);
		
		mRecognizer = VoiceRecognizer.createVoiceRecognizer(this, false);
		mRecognizer.setVoiceRecognizerListener(new MyVoiceRecognizerListener());
		
		btnMakeAnimal = (Button)findViewById(R.id.teachName_btn_newName);
		btnTeachName = (Button)findViewById(R.id.teachName_btn_teachStart);
		spinAnimals = (Spinner)findViewById(R.id.teachName_spin_action);
		
		mMakeDlg = new MakeAnimalDialog(this, new View.OnClickListener() {
			public void onClick(View v) {
				String name = mMakeDlg.getName();
				if(name==null || "".equals(name) || " ".equals(name) || name.contains(" ")) {
					Toast.makeText(TeachNameActivity.this, "올바른 이름을 입력해주세요", Toast.LENGTH_SHORT).show();
				} else {
					mData.addAnimal(name);
					animalNames = mData.getAnimalIDsAndNames();
					adapter.notifyDataSetChanged();
					dismissDialog(DLG_MAKE_ANIMAL);
				}
			}
		});
		
		mRecogDlg = new ListeningDialog(this);
		
		btnMakeAnimal.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(DLG_MAKE_ANIMAL);
			}
		});
		
		spinAnimals.setPrompt("Select Animal");
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, animalNames);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinAnimals.setAdapter(adapter);
		spinAnimals.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {}
			public void onNothingSelected(AdapterView<?> arg0) {
				new AlertDialog.Builder(TeachNameActivity.this).setTitle("경고")
					.setMessage("동물을 선택해주세요!")
					.setPositiveButton("확인", null)
					.show();
			}
			
		});
		
		btnTeachName.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String animalInfo = (String)spinAnimals.getSelectedItem();
				if(animalInfo == null || animalInfo.equals("") ||
						animalInfo.equals(" ")) {
					new AlertDialog.Builder(TeachNameActivity.this).setTitle("경고")
					.setMessage("동물을 선택해주세요!")
					.setPositiveButton("확인", null)
					.show();
					return;
				}
				mRecognizer.start();
				showDialog(DLG_RECOGNITION);
			}
		});
	}
	
	private class MyVoiceRecognizerListener implements VoiceRecognizerListener {
		
		@Override
		public void onVolumeChanged(int step) {}
		
		@Override
		public void onTimeoutRecognition() {}
		
		@Override
		public void onStartRecognition() {}
		
		@Override
		public void onResults(final ArrayList<String> results) {
			StringTokenizer token = new StringTokenizer((String)spinAnimals.getSelectedItem(), ":");
			final int animalId = Integer.parseInt(token.nextToken());
			final String name = token.nextToken();
			new AlertDialog.Builder(TeachNameActivity.this).setTitle("동물 이름 인식 완료")
				.setMessage("인식된 음성으로 "+name+"의 이름을 저장하시겠습니까?")
				.setPositiveButton("예", new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						mRecogDlg.setText("saving...");
						mData.getAnimalInfo(animalId).addVoiceNames(results);
						
						Toast.makeText(TeachNameActivity.this, "저장 완료", Toast.LENGTH_SHORT).show();
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
			Toast.makeText(TeachNameActivity.this, "Error Code : " + error, Toast.LENGTH_SHORT).show();
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
		case DLG_MAKE_ANIMAL:
			return mMakeDlg;
		case DLG_RECOGNITION:
			return mRecogDlg;
		default:
			return super.onCreateDialog(id);
		}
	}
	
	protected void onPrepareDialog(int id, android.app.Dialog dialog) {
		switch(id) {
		case DLG_MAKE_ANIMAL:
			mMakeDlg.resetEditText();
			break;
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
