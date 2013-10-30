package com.mthatcher.starcraft2wcs.entry;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.TextView;

public class ViewHolderData implements Parcelable{
	private boolean isGroupHolder;
	private String groupName;
	private String date;
	private String[] rank;
	private Bitmap[] flag;
	private Bitmap[] race;
	private String[] playerName;
	private String[] matchScore;
	private String[] mapScore;
	private int[] backgroundColor;
	private int size;
	
	public ViewHolderData(Parcel in) {
		isGroupHolder = in.readInt() == 1 ? true : false;
		groupName = in.readString();
		date = in.readString();
		rank = in.createStringArray();
		flag = in.createTypedArray(Bitmap.CREATOR);
		race = in.createTypedArray(Bitmap.CREATOR);
		playerName = in.createStringArray();
		matchScore = in.createStringArray();
		mapScore = in.createStringArray();
		backgroundColor = in.createIntArray();
		size = in.readInt();
	}
	
	public ViewHolderData(ViewHolder v) {
		isGroupHolder = v.isGroupHolder;
		groupName = v.groupName.getText().toString();
		date = v.date.getText().toString();
		rank = getStringArray(v.rank);
		flag = getBitmapArray(v.flag);
		race = getBitmapArray(v.race);
		playerName = getStringArray(v.playerName);
		matchScore = getStringArray(v.matchScore);
		mapScore = getStringArray(v.mapScore);
		backgroundColor = getColorArray(v.playerName);
		size = v.size;
	}

	public boolean isGroupHolder() {
		return isGroupHolder;
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

	public Bitmap[] getFlag() {
		return flag;
	}

	public Bitmap[] getRace() {
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

	public int[] getBackgroundColor() {
		return backgroundColor;
	}
	
	public int getSize() {
		return size;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(isGroupHolder ? 1 : 0); // Android has no "writeBoolean(...)" :(
		out.writeString(groupName);
		out.writeString(date);
		out.writeStringArray(rank);
		out.writeTypedArray(flag, flags);
		out.writeTypedArray(race, flags);
		out.writeStringArray(playerName);
		out.writeStringArray(matchScore);
		out.writeStringArray(mapScore);
		out.writeIntArray(backgroundColor);
		out.writeInt(size);
	}
	
    public static final Parcelable.Creator<ViewHolderData> CREATOR = new Parcelable.Creator<ViewHolderData>() {
        public ViewHolderData createFromParcel(Parcel in) {
            return new ViewHolderData(in);
        }

        public ViewHolderData[] newArray(int size) {
            return new ViewHolderData[size];
        }
    };

	private String[] getStringArray(ArrayList<TextView> list) {
		String[] ret = new String[list.size()];
		for(int i = 0; i < list.size(); i++){
			ret[i] = list.get(i).getText().toString();
		}
		return ret;
	}
	
	private int[] getColorArray(ArrayList<TextView> list) {
		int[] ret = new int[list.size()];
		for(int i = 0; i < list.size(); i++){
			ret[i] = ((ColorDrawable)list.get(i).getBackground()).getColor();
		}
		return ret;
	}
	
	private Bitmap[] getBitmapArray(ArrayList<TextView> list) {
		Bitmap[] ret = new Bitmap[list.size()];
		for(int i = 0; i < list.size(); i++){
			ret[i] = ((BitmapDrawable)list.get(i).getCompoundDrawables()[0]).getBitmap();
		}
		return ret;
	}
}
