package com.swm.vg.voiceprotoapp;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.swm.vg.data.ActionInfo;
import com.swm.vg.data.AnimalInfo;
import com.swm.vg.data.RecognizedData;
import com.swm.vg.voicetoactions.PatternMatcher;
import com.swm.vg.voicetoactions.VoiceRecognizer;
import com.swm.vg.voicetoactions.VoiceRecognizerListener;

public class CommunicateActivity extends Activity {
	private VoiceRecognizer mRecognizer;
	
	private TextView txtvResult, txtvAction, txtvWho, txtvInfo;
	private ImageView imgvMic, imgvLeftVolume[], imgvRightVolume[];
	private ProgressBar progAnal;
	
	private final RecognizedData mData;
	private final ArrayList<AnimalInfo> animalList;
	
	public static final int HANDLER_SET_RESULT = 1;
	public static final int HANDLER_SET_RESULT_ERROR = 2;
	public Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what) {
			case HANDLER_SET_RESULT:
				txtvResult.setText((String)msg.obj);
				if(msg.arg1 == -1) {
					txtvWho.setText("None");
					txtvAction.setText("None");
				} else {
					AnimalInfo nowAnimal = mData.getAnimalInfo(msg.arg1);
					txtvWho.setText(""+msg.arg1 + ':' + nowAnimal.getName());
					txtvAction.setText(""+msg.arg2);
				}
				break;
			case HANDLER_SET_RESULT_ERROR:
				txtvWho.setText("None");
				txtvAction.setText("None");
				break;
			}
		}
	};
	
	private void setCommunicateResult(String result, int Who, int Action) {
		mHandler.sendMessage(Message.obtain(mHandler,
				HANDLER_SET_RESULT, Who, Action, result));
	}
	
	private void analyzeResult(String result) {
		//먼저 동물이름 있는지 검사하자
		ActionInfo action = PatternMatcher.patternMatch(result, animalList);
		
		setCommunicateResult(result, action.animalId, action.actionId);
	}
	
	private void makeSimpleAlertDlg(String msg) {
		new AlertDialog.Builder(this).setTitle("경고")
			.setMessage(msg)
			.setPositiveButton("확인", null)
			.show();
	}
	
	public CommunicateActivity() {
		mData = RecognizedData.sharedRecognizedData();
		animalList = mData.getAnimalList();
	}
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		setContentView(R.layout.activity_communicate);
		
		txtvResult = (TextView)findViewById(R.id.actCommu_txt_result);
		txtvWho = (TextView)findViewById(R.id.actCommu_txt_Who);
		txtvAction = (TextView)findViewById(R.id.actCommu_txt_Action);
		txtvInfo = (TextView)findViewById(R.id.actCommu_text_info);
		imgvMic =  (ImageView)findViewById(R.id.actCommu_img_mic);
		progAnal = (ProgressBar)findViewById(R.id.actCommu_progress_recog);
		imgvLeftVolume = new ImageView[3];
		imgvRightVolume = new ImageView[3];
		for(int i=0; i<3; i++){
			imgvLeftVolume[i] = (ImageView)findViewById(R.id.actCommu_img_volume_left_1+i);
			imgvRightVolume[i] = (ImageView)findViewById(R.id.actCommu_img_volume_right_1+i);
		}
		initVolumeImg();
		
		mRecognizer = VoiceRecognizer.createVoiceRecognizer(this, true);
		mRecognizer.setVoiceRecognizerListener(new MyVoiceRecognitionListener());
		
		mRecognizer.start();
	}
	
	private void setVolumeImg(int step){
		for(int i=0; i<3; i++){
			if(i<step) {
				imgvLeftVolume[i].setVisibility(View.VISIBLE);
				imgvRightVolume[i].setVisibility(View.VISIBLE);
			} else{
				imgvLeftVolume[i].setVisibility(View.INVISIBLE);
				imgvRightVolume[i].setVisibility(View.INVISIBLE);
			}
		}
	}
	
	private void initVolumeImg() {
		for(int i=0; i<3; i++) {
			imgvLeftVolume[i].setVisibility(View.INVISIBLE);
			imgvRightVolume[i].setVisibility(View.INVISIBLE);
		}
	}
	
    @Override
    protected void onResume() {
    	Log.d("VoiceRecognitionListener", "onResume");
    	mRecognizer.resume();
    	super.onResume();
    }
    
    @Override
    protected void onPause() {
    	Log.d("VoiceRecognitionListener", "onPause");
    	mRecognizer.pause();
    	super.onPause();
    }

    @Override
    protected void onDestroy() {
    	Log.d("VoiceRecognitionListener", "onDestroy");
    	mRecognizer.closeVoiceRecognizer();
    	super.onDestroy();
    }
    
	private class MyVoiceRecognitionListener implements VoiceRecognizerListener {
		@Override
		public void onResults(ArrayList<String> results) {
			Log.d("VoiceRecognitionListener", "onResults");
			if(results.size()<=0) {
				mHandler.sendEmptyMessage(HANDLER_SET_RESULT_ERROR);
				return;
			} else {
				analyzeResult(results.get(0));
			}
		}

		@Override
		public void onVolumeChanged(int step) {
			setVolumeImg(step);
		}

		@Override
		public void onStartRecognition() {
			Log.d("VoiceRecognitionListener", "onTimeoutRecognition");
			txtvInfo.setText("start recognize");
		}

		@Override
		public void onTimeoutRecognition() {
			Log.d("VoiceRecognitionListener", "onTimeoutRecognition");
			txtvInfo.setText("timeout! next recognize");
		}

		@Override
		public void onBeginningOfSppech() {
			Log.d("VoiceRecognitionListener", "onBeginningOfSppech");
			txtvInfo.setText("voice recognizing...");
		}

		@Override
		public void onReady() {
			Log.d("VoiceRecognitionListener", "onReady");
			txtvInfo.setText("recognization ready");
			progAnal.setVisibility(View.INVISIBLE);
			imgvMic.setVisibility(View.VISIBLE);
		}

		@Override
		public void onEndOfSpeech() {
			Log.d("VoiceRecognitionListener", "onEndOfSpeech");
			txtvInfo.setText("recognizing end");
			progAnal.setVisibility(View.VISIBLE);
			imgvMic.setVisibility(View.INVISIBLE);
		}

		@Override
		public void onFinishRecognition() {
			Log.d("VoiceRecognitionListener", "onFinishRecognition");
			txtvInfo.setText("finish recognizing");
		}

		@Override
		public void onCancelRecognition() {
			Log.d("VoiceRecognitionListener", "onCancelRecognition");
		}

		@Override
		public void onClosing() {
			Log.d("VoiceRecognitionListener", "onClosing");
		}

		@Override
		public void onError(int error) {
			String msg = null;
			
			switch (error) {
			case SpeechRecognizer.ERROR_AUDIO:
				msg = "오디오 입력 중 오류가 발생했습니다.";
				break;
			case SpeechRecognizer.ERROR_CLIENT:
				msg = "단말에서 오류가 발생했습니다.";
				break;
			case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
				msg = "권한이 없습니다.";
				break;
			case SpeechRecognizer.ERROR_NETWORK:
			case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
				msg = "네트워크 오류가 발생했습니다.";
				break;
			case SpeechRecognizer.ERROR_NO_MATCH:
				msg = "일치하는 항목이 없습니다.";
				break;
			case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
				msg = "음성인식 서비스가 과부하 되었습니다.";
				break;
			case SpeechRecognizer.ERROR_SERVER:
				msg = "서버에서 오류가 발생했습니다.";
				break;
			case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
				msg = "입력이 없습니다.";
				break;
			default:
				msg = "알 수 없는 에러가 발생했습니다.";
				break;
			}
			
			mHandler.sendEmptyMessage(HANDLER_SET_RESULT_ERROR);
			Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
		}
	}
}
