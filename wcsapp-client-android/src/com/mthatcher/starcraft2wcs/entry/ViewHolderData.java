package com.mthatcher.starcraft2wcs.entry;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;
import android.widget.TextView;

public class ViewHolderData{
	private boolean isGroupEntry;
	private String groupName;
	private String date;
	private String[] rank;
	private Drawable[] flag;
	private Drawable[] race;
	private String[] playerName;
	private String[] matchScore;
	private String[] mapScore;
	private Drawable[] backgroundColor;
	private int size;
	
	public ViewHolderData(ViewHolder v) {
		groupName = v.groupName.getText().toString();
		date = v.date.getText().toString();
		rank = getStringArray(v.rank);
		flag = getDrawableArray(v.flag);
		race = getDrawableArray(v.race);
		playerName = getStringArray(v.playerName);
		matchScore = getStringArray(v.matchScore);
		mapScore = getStringArray(v.mapScore);
		backgroundColor = getBackground(v.playerName);
		isGroupEntry = v.isGroupHolder;
		size = v.size;
	}
	
	public String getGroupName() {
		return groupName;
	}

	public String getDate() {
		return date;
	}

	public String[] getRank() {
		return rank;
	}

	public Drawable[] getFlag() {
		return flag;
	}

	public Drawable[] getRace() {
		return race;
	}

	public String[] getPlayerName() {
		return playerName;
	}

	public String[] getMatchScore() {
		return matchScore;
	}

	public String[] getMapScore() {
		return mapScore;
	}

	public Drawable[] getBackgroundColor() {
		return backgroundColor;
	}
	
	public int getSize() {
		return size;
	}
	
	public boolean isGroupEntry() {
		return isGroupEntry;
	}

	private String[] getStringArray(ArrayList<TextView> list) {
		String[] ret = new String[list.size()];
		for(int i = 0; i < list.size(); i++){
			ret[i] = list.get(i).getText().toString();
		}
		return ret;
	}
	
	private Drawable[] getBackground(ArrayList<TextView> list) {
		Drawable[] ret = new Drawable[list.size()];
		for(int i = 0; i < list.size(); i++){
			ret[i] = list.get(i).getBackground();
		}
		return ret;
	}
	
	private Drawable[] getDrawableArray(ArrayList<TextView> list) {
		Drawable[] ret = new Drawable[list.size()];
		for(int i = 0; i < list.size(); i++){
			ret[i] = list.get(i).getCompoundDrawables()[0];
		}
		return ret;
	}
}
