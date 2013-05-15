package com.mthatcher.starcraft2wcs;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class WcsDBHelper extends SQLiteOpenHelper {
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "sc2wcs.sqlite";
	private String deleteSQL = "";
	private String createSQL = "";
	
	public WcsDBHelper(Context context, String cSQL) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		createSQL = cSQL;
		deleteSQL = "DROP TABLE IF EXISTS matches; DROP TABLE IF EXISTS games; DROP TABLE IF EXISTS schedule;";
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(createSQL);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(deleteSQL);
		onCreate(db);
	}
}
