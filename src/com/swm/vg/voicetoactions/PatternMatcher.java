package com.swm.vg.voicetoactions;

import java.util.ArrayList;

import com.swm.vg.data.ActionInfo;
import com.swm.vg.data.ActionList;
import com.swm.vg.data.AnimalInfo;


public class PatternMatcher {
	
	public static ActionInfo patternMatch(String sentence, ArrayList<AnimalInfo> arrAnimal) {
		int animalId = -1, actionId = ActionList.ACTION_EXTRA_UNKNOWN;
		String str = MyUtils.convertNoSpace(sentence);
		
		int maxLen = 0;
		String seletedName = null;
		AnimalInfo seletedAnimal = null;
		for(AnimalInfo animal : arrAnimal) {
			final ArrayList<String> arrVoiceNames = animal.getVoiceNames();
			for(String name : arrVoiceNames) {
				if(str.contains(name)) {
					//최장매치방식
					int nowLen = name.length();
					if(nowLen > maxLen) {
						maxLen = nowLen;
						seletedAnimal = animal;
						seletedName = name;
						
					}
				}
			}
		}
		
		if(seletedAnimal != null) {
			animalId = seletedAnimal.getId();
			String cutStr = "";
			String[] cuts = str.split(seletedName);
			ArrayList<AnimalInfo.AnimalAction> arrAction = seletedAnimal.getActionList();

			maxLen = 0;
			AnimalInfo.AnimalAction seletedAction = null;
			for(AnimalInfo.AnimalAction action : arrAction) {
				final ArrayList<AnimalInfo.AnimalAction.ActionVoice> arrActionVoice = action.getVoiceList();
				for(AnimalInfo.AnimalAction.ActionVoice actionVoice : arrActionVoice) {
					//동물이름 뺀 부분에서 액션이 있나 매칭시키기
					boolean bContain = false;
					String voice = actionVoice.getVoiceString();
					for(String cut : cuts) {
						if(cut.contains(voice)) {
							bContain = true;
							break;
						}
					}
					
					int maxNum = 0;
					if(bContain) {
						//최장매치
						int nowLen = voice.length();
						if(nowLen > maxLen) {
							maxLen = nowLen;
							seletedAction = action;
						} else if (nowLen == maxLen) {
							//최장매치해도 같은 숫자면 다음엔 가르친 number
							int nowNum = seletedAction.getActionCount();
							if(nowNum > maxNum) {
								maxNum = nowNum;
								seletedAction = action;
							} else if (nowNum == maxNum) {
								//가르친 수까지 같으면...?
								if((int)(Math.random())%2 == 0) {
									seletedAction = action;
								}
							}
						}
					}
				}
			}
			
			if(seletedAction != null) {
				actionId = seletedAction.getActionId();
			}
		}
		
		return new ActionInfo(animalId, actionId);
	}
	
	public static ActionInfo patternMatchOriginal(String sentence, ArrayList<AnimalInfo> arrAnimal) {
		int animalId = -1, actionId = ActionList.ACTION_EXTRA_UNKNOWN;
		String str = MyUtils.convertNoSpace(sentence);
		
		int maxLen = 0;
		String seletedName = null;
		AnimalInfo seletedAnimal = null;
		for(AnimalInfo animal : arrAnimal) {
			final ArrayList<String> arrVoiceNames = animal.getVoiceNames();
			for(String name : arrVoiceNames) {
				if(str.contains(name)) {
					//최장매치방식
					int nowLen = name.length();
					if(nowLen > maxLen) {
						maxLen = nowLen;
						seletedAnimal = animal;
						seletedName = name;
					}
				}
			}
		}
		
		if(seletedAnimal != null) {
			animalId = seletedAnimal.getId();
			String cutStr = "";
			String[] cuts = str.split(seletedName);
			ArrayList<AnimalInfo.AnimalAction> arrAction = seletedAnimal.getActionList();

			maxLen = 0;
			AnimalInfo.AnimalAction seletedAction = null;
			for(AnimalInfo.AnimalAction action : arrAction) {
				final ArrayList<AnimalInfo.AnimalAction.ActionVoice> arrActionVoice = action.getVoiceList();
				for(AnimalInfo.AnimalAction.ActionVoice actionVoice : arrActionVoice) {
					boolean bContain = false;
					String voice = actionVoice.getVoiceString();
					for(String cut : cuts) {
						if(cut.contains(voice)) {
							bContain = true;
							break;
						}
					}
					
					if(bContain) {
						int nowLen = voice.length();
						if(nowLen > maxLen) {
							maxLen = nowLen;
							seletedAction = action;
						}
					}
				}
			}
			
			if(seletedAction != null) {
				actionId = seletedAction.getActionId();
			}
		}
		
		return new ActionInfo(animalId, actionId);
	}
	
}
