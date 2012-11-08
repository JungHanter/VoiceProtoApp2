package com.swm.vg.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.StringTokenizer;

import com.swm.vg.voicetoactions.MyUtils;

import android.util.Log;
import android.widget.Toast;

public class RecognizedData {
	public final static String DIR_DATA_ROOT = MyUtils.EXT_STORAGE + "/data/com.swm.vg";
	public final static String DIR_NAMES_ROOT = DIR_DATA_ROOT + "/names";
	public final static String DIR_ACTIONS_ROOT = DIR_DATA_ROOT + "/patterns";
	public final static String FILE_ANIMAL_LIST = "animals.txt";
	
	private int lastId = -1;
	ArrayList<AnimalInfo> animalList = null;
	
	
	public void addAnimal(String name) {
		lastId += 1;
		String animalInfo = "" + lastId + ':' + name;
		
		boolean isFirst = false;
		File file = new File(DIR_NAMES_ROOT + '/' + FILE_ANIMAL_LIST);
		if(!file.exists()) {
			isFirst = true;
			new File(DIR_NAMES_ROOT).mkdirs();
		}
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file, true),"utf-8"));
			if(isFirst) bw.append(animalInfo);
			else bw.append('\n' + animalInfo);
			bw.flush();
			
			animalList.add(AnimalInfo.makeNewAnimal(lastId, name));
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try { bw.close(); }
			catch(Exception e) {}
		}
	}
	
	public AnimalInfo getAnimalInfo(int id) {
		for(AnimalInfo animal : animalList) {
			if(animal.getId() == id) {
				return animal;
			}
		}
		return null;
	}
	
	public ArrayList<String> getAnimalIDsAndNames() {
		ArrayList<String> arrAnimal = new ArrayList<String>();
		for(AnimalInfo animal : animalList) {
			arrAnimal.add(""+animal.getId() + ":" + animal.getName());
		}
		return arrAnimal;
	}
	
	public ArrayList<AnimalInfo> getAnimalList() {
		return animalList;
	}
	
	public ArrayList<AnimalInfo> loadAnimalList() {
		animalList = new ArrayList<AnimalInfo>();
		
		File file = new File(DIR_NAMES_ROOT + '/' + FILE_ANIMAL_LIST);
		if(file.exists()) {
			BufferedReader bm = null;
			try {
				bm = new BufferedReader(new InputStreamReader(
						new FileInputStream(file), "utf-8"));
				String line = null;
				while( (line=bm.readLine()) != null ) {
					int id = Integer.parseInt(new StringTokenizer(line, ":").nextToken());
					animalList.add(new AnimalInfo(id));
				}
			} catch(Exception e) {
				e.printStackTrace();
			} finally {
				try { bm.close(); }
				catch(Exception e) {}
			}
		}
		
		if(animalList.size() == 0) {
			lastId = 0;
		} else {
			Collections.sort(animalList);
			lastId = animalList.get(animalList.size()-1).getId();
		}
		
		return animalList;
	}
	
	private static RecognizedData mInstance = null;
	public static RecognizedData sharedRecognizedData() {
		if(mInstance == null) mInstance = new RecognizedData();
		return mInstance;
	}
	private RecognizedData() {
		Log.i("Recognized Data", "Create shared Recognized Data");
		lastId = -1;
		animalList = null;
	}
}
