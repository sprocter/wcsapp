package com.mthatcher.starcraft2wcs;

import android.app.Application;

public class AppClass extends Application{
	private static WcsDBHelper db;

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
}
