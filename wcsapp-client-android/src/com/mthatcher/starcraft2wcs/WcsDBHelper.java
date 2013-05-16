package com.mthatcher.starcraft2wcs;

import java.util.StringTokenizer;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class WcsDBHelper extends SQLiteOpenHelper {
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "sc2wcs.sqlite";
	private String DELETE_SQL = "DROP TABLE IF EXISTS matches; DROP TABLE IF EXISTS games; DROP TABLE IF EXISTS schedule;";
	
	public WcsDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	public void updateDB(SQLiteDatabase db, String batchSQL){
		splitAndRunQueries(db, DELETE_SQL);
		splitAndRunQueries(db, batchSQL);
	}

	private void splitAndRunQueries(SQLiteDatabase db, String batchSQL) {
		StringTokenizer tok = new StringTokenizer(batchSQL, ";");
		String temp;
		while(tok.hasMoreTokens()){
			temp = tok.nextToken();
			db.execSQL(temp);
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

	}
	
	@Override
	public void onOpen(SQLiteDatabase db) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	
	}
}
