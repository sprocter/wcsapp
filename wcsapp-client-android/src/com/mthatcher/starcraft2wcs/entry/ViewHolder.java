package com.mthatcher.starcraft2wcs.entry;

import android.widget.TextView;

public class ViewHolder{
	public boolean isGroupHolder;
	public TextView groupName;
	public TextView date;
	public TextView rank[] = new TextView[4];
	public TextView flag[] = new TextView[8];
	public TextView race[] = new TextView[8];
	public TextView playerName[] = new TextView[8];
	public TextView matchScore[] = new TextView[4];
	public TextView mapScore[] = new TextView[8];
}