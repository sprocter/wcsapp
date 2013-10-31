package com.mthatcher.starcraft2wcs;

import android.app.Application;

import com.mthatcher.starcraft2wcs.entry.ViewHolderData;

public class AppClass extends Application{
	private static WcsDBHelper db;
	private static ViewHolderData vhd;

	@Override
	public void onCreate() {
		super.onCreate();
		db = new WcsDBHelper(this);
	}
	
	@Override
	public void onTerminate() {
		super.onTerminate();
		db.close();
		db = null;
	}
	
	public static WcsDBHelper getDBHelper(){
		return db;
	}
	
	public static ViewHolderData getVhd() {
		return vhd;
	}

	public static void setVhd(ViewHolderData vhd) {
		AppClass.vhd = vhd;
	}
}
