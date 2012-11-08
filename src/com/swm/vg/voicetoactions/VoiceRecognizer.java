package com.swm.vg.voicetoactions;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class VoiceRecognizer {
	private SpeechRecognizer mRecognizer = null;
	private VoiceRecognizerListener mListener = null;
	
	private final boolean bLoopingRecognizer;
	
	private boolean bRecogUsing = false;
	private boolean onSpeeching = false;
	private boolean bPause = false;
	
	public static final int MSG_START_RECOG_LOOP = 0;
	public static final int MSG_START_RECOG_ONCE = 1;
	public static final int MSG_RECOG_TIMEOUT = 2;
	public static final int MSG_STATE_READY = 3;
	public static final int MSG_STATE_END = 4;
	public static final int MSG_STATE_FINISH = 5;
	public static final int MSG_RECOG_CANCEL = 6;
	public static final int MSG_RECOG_NOT_SUPPORT = 101;
	
	private static final int RECOG_RESPONSE_TIMEOUT = 3000;	//인식 후 대기시간 3초
	private static final int RECOG_ONCE_RESPONSE_TIMEOUT = 5000;	//인식 후 대기시간 5초
	private static final int RECOG_WAITING_TIMEOUT = 5000;	//5초마다 음성인식 새로
	private static final int RECOG_ONCE_WAITING_TIMEOUT = 5000;	//음성입력 대기시간 5초

	private Handler mSpeechStatusHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case MSG_START_RECOG_LOOP:
				mListener.onStartRecognition();
				startVoiceRecognition();
				sendEmptyMessageDelayed(MSG_RECOG_TIMEOUT, RECOG_WAITING_TIMEOUT);
				break;
			case MSG_START_RECOG_ONCE:
				mListener.onStartRecognition();
				startVoiceRecognition();
				sendEmptyMessageDelayed(RECOG_ONCE_WAITING_TIMEOUT, RECOG_WAITING_TIMEOUT);
				break;
			case MSG_RECOG_TIMEOUT:
				if(!onSpeeching) {
					mListener.onTimeoutRecognition();
					if(bLoopingRecognizer) {
						cancelVoiceRecognition();
						sendEmptyMessage(MSG_START_RECOG_LOOP);
					} else {
						cancelVoiceRecognition();
					}
				}
				break;
			case MSG_STATE_READY:
				mListener.onReady();
				break;
			case MSG_STATE_END:
				mListener.onEndOfSpeech();
				if(bLoopingRecognizer)
					sendEmptyMessageDelayed(MSG_STATE_FINISH, RECOG_RESPONSE_TIMEOUT);	//인식후 응답 대기시간.
				else
					sendEmptyMessageDelayed(MSG_STATE_FINISH, RECOG_ONCE_RESPONSE_TIMEOUT);
				break;
			case MSG_STATE_FINISH:
				stopVoiceRecognition();
				if(bLoopingRecognizer)
					sendEmptyMessage(MSG_START_RECOG_LOOP);
				break;
			case MSG_RECOG_CANCEL:
				mListener.onCancelRecognition();
				cancelVoiceRecognition();
				break;
			default:
				break;
			}
		}
	};
	
	private void init() {
		mRecognizer = SpeechRecognizer.createSpeechRecognizer(mParent);		//음성인식 객체
		mRecognizer.setRecognitionListener(new MyRecognitionListener());	//음성인식 리스너 등록
	}
	
	public void start() {
		if(bLoopingRecognizer) {	//음성인식 루프 -> 명령내리는 메인화면
			mSpeechStatusHandler.sendEmptyMessage(MSG_START_RECOG_LOOP);
		} else {	//한번만 인식 -> 말 가르치기
			mSpeechStatusHandler.sendEmptyMessage(MSG_START_RECOG_ONCE);
		}
	}
	
	public void cancel() {
		mSpeechStatusHandler.sendEmptyMessage(MSG_RECOG_CANCEL);
	}

	private void startVoiceRecognition() {
		bRecogUsing = true;
		onSpeeching = false;
//		mRecognizer = SpeechRecognizer.createSpeechRecognizer(MainActivity.this);		//음성인식 객체
//		mRecognizer.setRecognitionListener(new MyRecognitionListener());				//음성인식 리스너 등록
		
		Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);		//음성인식 intent생성
		i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, mParent.getPackageName());	//데이터 설정
		i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");					//음성인식 언어 설정
	
		mRecognizer.startListening(i);
	}
	
	private void releaseVoiceRecognitionMessage() {
		mSpeechStatusHandler.removeMessages(MSG_RECOG_TIMEOUT);
		mSpeechStatusHandler.removeMessages(MSG_STATE_END);
		mSpeechStatusHandler.removeMessages(MSG_STATE_FINISH);
	}
	
	private void stopVoiceRecognition() {
		mRecognizer.stopListening();
		releaseVoiceRecognitionMessage();
	}
	
	private void cancelVoiceRecognition() {
		bRecogUsing = false;
		mRecognizer.cancel();
		releaseVoiceRecognitionMessage();
	}
	
	private class MyRecognitionListener implements RecognitionListener {
		// 음성 인식 결과 받음
		@Override
		public void onResults(Bundle results) {
			Log.d("Sppech Listener", "onResults");
			ArrayList<String> textList = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
			mListener.onResults(textList);
			
			releaseVoiceRecognitionMessage();
			mListener.onFinishRecognition();
			
			if(bLoopingRecognizer)
				mSpeechStatusHandler.sendEmptyMessage(MSG_START_RECOG_LOOP);
		}
				
		// 입력 소리 변경 시
		@Override
		public void onRmsChanged(float rmsdB) {
//			Log.d("Sppech Listener", "onRmsChanged - " + rmsdB);
			int step = (int)(rmsdB/7);		//소리 크기에 따라 step을 구함. 총 4단계(0~4)
			if(step>4) step = 4;
//			mListener.onVolumeChanged(step);
		}

		// 음성 인식 준비가 되었으면
		@Override
		public void onReadyForSpeech(Bundle params) {
			Log.d("Sppech Listener", "onReadyForSpeech");
			mSpeechStatusHandler.sendEmptyMessage(MSG_STATE_READY);
		}

		// 음성 입력이 끝났으면
		@Override
		public void onEndOfSpeech() {
			Log.d("Sppech Listener", "onEndOfSpeech");
			mSpeechStatusHandler.sendEmptyMessage(MSG_STATE_END);
		}

		// 에러가 발생하면
		@Override
		public void onError(int error) {
			Log.d("Sppech Listener", "onError");
			if(bRecogUsing)
				mListener.onError(error);
			mListener.onFinishRecognition();
		}

		// 입력이 시작되면
		@Override
		public void onBeginningOfSpeech() {
			Log.d("Sppech Listener", "onBeginningOfSpeech");
			onSpeeching = true;
			mListener.onBeginningOfSppech();
		}

		// 인식 결과의 일부가 유효할 때
		@Override
		public void onPartialResults(Bundle partialResults) {}
		
		// 더 많은 소리를 받을 때
		@Override
		public void onBufferReceived(byte[] buffer) {} 
		
		// 미래의 이벤트를 추가하기 위해 미리 예약되어진 함수
		@Override
		public void onEvent(int eventType, Bundle params) {}		
	};
	
	
	public void pause() {
		if(!bLoopingRecognizer) {
			if(bRecogUsing) cancelVoiceRecognition();
			return;
		}
		if(!bPause) {
			bPause = true;
			if(mRecognizer != null)
				cancelVoiceRecognition();
		}
	}
	
	public void resume() {
		if(!bLoopingRecognizer) return;
		if(bPause) {
			bPause = false;
			if(mRecognizer != null)
				mSpeechStatusHandler.sendEmptyMessage(MSG_START_RECOG_LOOP);
		}
	}
	
	
	//생성 관련
	public static boolean bUsingRecognizer = false;
	
	public void setVoiceRecognizerListener(VoiceRecognizerListener recogListener) {
		mListener = recogListener;
	}
	
	public void closeVoiceRecognizer() {
		mListener.onClosing();
		if(mRecognizer != null) {
			cancelVoiceRecognition();
			mRecognizer.destroy();
		}
		mRecognizer = null;
		bUsingRecognizer = false;
	}
	
	//이미 실행중이면 return null;
	public static VoiceRecognizer createVoiceRecognizer(Activity parent, boolean bLooping) {
		if(bUsingRecognizer) return null;
		else {
			bUsingRecognizer = true;
			return new VoiceRecognizer(parent, bLooping);
		}
	}
	
	public static boolean isSupport(Context ctx) {
		PackageManager pm = ctx.getPackageManager();
		List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(
				RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		if (activities.size() == 0) return false;
		return true;
	}
	
	private final Activity mParent;
	private VoiceRecognizer(Activity parent, boolean bLooping) {
		mParent = parent;
		bLoopingRecognizer = bLooping;
		init();
	}
}
