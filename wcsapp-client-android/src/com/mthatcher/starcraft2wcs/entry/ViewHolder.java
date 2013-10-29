package com.mthatcher.starcraft2wcs.entry;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.TextView;

public class ViewHolder{
	public boolean isGroupHolder;
	public TextView groupName;
	public TextView date;
	public ArrayList<TextView> rank = new ArrayList<TextView>();
	public ArrayList<TextView> flag = new ArrayList<TextView>();
	public ArrayList<TextView> race = new ArrayList<TextView>();
	public ArrayList<TextView> playerName = new ArrayList<TextView>();
	public ArrayList<TextView> matchScore = new ArrayList<TextView>();
	public ArrayList<TextView> mapScore = new ArrayList<TextView>();
	public int size;
}