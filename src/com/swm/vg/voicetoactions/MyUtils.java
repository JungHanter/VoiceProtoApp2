package com.swm.vg.voicetoactions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.StringTokenizer;

import android.os.Environment;

public class MyUtils {
	private static void setCharterSet(String charSet) throws Exception {
		Class<Charset> c = Charset.class;
		try {
			Field defaultCharsetField = c.getDeclaredField("defaultCharset"); 
			defaultCharsetField.setAccessible(true);
			defaultCharsetField.set(null, Charset.forName(charSet));
		} catch (Exception e) {}
	}
	
	public final static String EXT_STORAGE = Environment.getExternalStorageDirectory().getAbsolutePath();
	
	public final static String FILE_EXTENSION_GAMEDATA = ".txt";
	public final static FilenameFilter FILE_FILTER_GAMEDATA = new FilenameFilter() {
		public boolean accept(File dir, String filename) {
			return filename.endsWith(FILE_EXTENSION_GAMEDATA);
		}
	};
	
	public static String convertNoSpace(String origin) {
		StringTokenizer token = new StringTokenizer(origin, " ");
		StringBuilder newStr = new StringBuilder();
		try {
			while( token.hasMoreTokens() ) {
				newStr.append(token.nextToken());
			}
		} catch (Exception e) { e.printStackTrace(); }
		return newStr.toString();
	}
}
