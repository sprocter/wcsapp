package com.mthatcher.starcraft2wcs.entry;

import android.view.View;
import android.widget.TextView;

import com.mthatcher.starcraft2wcs.LandingPage.MatchResult;
import com.mthatcher.starcraft2wcs.LandingPage.Race;
import com.mthatcher.starcraft2wcs.R;

public class GroupEntry implements GroupOrBracketEntry{
	private String name;
	private String country;
	private Race race;
	private int place;
	private int matchesWon;
	private int matchesLost;
	private int mapsWon;
	private int mapsLost;
	private MatchResult result;

	public GroupEntry(String name, String country, String race,
			String place, int matchesWon, int matchesLost, int mapsWon,
			int mapsLost, String result) {
		this.name = name;
		this.country = country;
		this.race = EntryUtil.getRaceFromString(race);
		this.place = place.length() > 0 ? Integer.parseInt(place) : -1;
		this.matchesWon = matchesWon;
		this.matchesLost = matchesLost;
		this.mapsWon = mapsWon;
		this.mapsLost = mapsLost;
		this.result = getResultFromString(result);
	}

	public int getBackgroundColor() {
		switch (result) {
		case WIN:
			return 0xFFCCFFCC;
		case LOSE:
			return 0xFFFFDDAA;
		default:
			return 0xFFFFFFFF;
		}
	}
	
	public Race getRace(){
		return race;
	}

	private MatchResult getResultFromString(String result) {
		if (result.equalsIgnoreCase("up"))
			return MatchResult.WIN;
		else if (result.equalsIgnoreCase("staydown"))
			return MatchResult.LOSE;
		else
			return MatchResult.NOTYETPLAYED;
	}

	public String getName() {
		return name;
	}

	public int getPlace() {
		return place;
	}

	public int getMatchesWon() {
		return matchesWon;
	}

	public int getMatchesLost() {
		return matchesLost;
	}

	public int getMapsWon() {
		return mapsWon;
	}

	public int getMapsLost() {
		return mapsLost;
	}

	public static ViewHolder getHolder(View convertView) {
		ViewHolder holder = new ViewHolder();
		
		holder.isGroupHolder = true;
		
		holder.groupName = (TextView) convertView.findViewById(R.id.schedule_name);
		holder.date = (TextView) convertView.findViewById(R.id.schedule_date);
		
		holder.playerName[0] = (TextView) convertView.findViewById(R.id.group_player_1_name);
		holder.playerName[1] = (TextView) convertView.findViewById(R.id.group_player_2_name);
		holder.playerName[2] = (TextView) convertView.findViewById(R.id.group_player_3_name);
		holder.playerName[3] = (TextView) convertView.findViewById(R.id.group_player_4_name);
		
		holder.rank[0] = (TextView) convertView.findViewById(R.id.group_player_1_rank);
		holder.rank[1] = (TextView) convertView.findViewById(R.id.group_player_2_rank);
		holder.rank[2] = (TextView) convertView.findViewById(R.id.group_player_3_rank);
		holder.rank[3] = (TextView) convertView.findViewById(R.id.group_player_4_rank);
		
		holder.flag[0] = (TextView) convertView.findViewById(R.id.group_player_1_flag);
		holder.flag[1] = (TextView) convertView.findViewById(R.id.group_player_2_flag);
		holder.flag[2] = (TextView) convertView.findViewById(R.id.group_player_3_flag);
		holder.flag[3] = (TextView) convertView.findViewById(R.id.group_player_4_flag);
		
		holder.race[0] = (TextView) convertView.findViewById(R.id.group_player_1_race);
		holder.race[1] = (TextView) convertView.findViewById(R.id.group_player_2_race);
		holder.race[2] = (TextView) convertView.findViewById(R.id.group_player_3_race);
		holder.race[3] = (TextView) convertView.findViewById(R.id.group_player_4_race);
		
		holder.matchScore[0] = (TextView) convertView.findViewById(R.id.group_player_1_match_score);
		holder.matchScore[1] = (TextView) convertView.findViewById(R.id.group_player_2_match_score);
		holder.matchScore[2] = (TextView) convertView.findViewById(R.id.group_player_3_match_score);
		holder.matchScore[3] = (TextView) convertView.findViewById(R.id.group_player_4_match_score);
		
		holder.mapScore[0] = (TextView) convertView.findViewById(R.id.group_player_1_map_score);
		holder.mapScore[1] = (TextView) convertView.findViewById(R.id.group_player_2_map_score);
		holder.mapScore[2] = (TextView) convertView.findViewById(R.id.group_player_3_map_score);
		holder.mapScore[3] = (TextView) convertView.findViewById(R.id.group_player_4_map_score);
		
		return holder;
	}
}
