package com.swm.vg.voicetoactions;

import java.util.ArrayList;

public interface VoiceRecognizerListener {
	//	public void onPreMessage(int what);
//	public void onPostMessage(int what);
	
	public void onResults(ArrayList<String> results);	//음성인식 결과 리스트
	public void onVolumeChanged(int step); 			//0~4
	
	public void onStartRecognition();				//음성인식루프 시작될때
	public void onTimeoutRecognition();				//음성인식루프 타임아웃
	public void onBeginningOfSppech();				//음성입력이 시작되면
	public void onReady();							//음성 인식 준비가 되면
	public void onEndOfSpeech();					//음성입력(사람이 말하는게)이 끝났을 때
	public void onFinishRecognition();				//음성인식루프가 종료될때
	public void onCancelRecognition();				//음성인식루프 취소될때
	public void onClosing();						//음성인식을 닫을때
	public void onError(int error);					//에러나면
}
