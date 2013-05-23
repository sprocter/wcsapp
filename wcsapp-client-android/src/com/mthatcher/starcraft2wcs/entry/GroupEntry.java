package com.mthatcher.starcraft2wcs.entry;

import android.view.View;
import android.widget.TextView;

import com.mthatcher.starcraft2wcs.LandingPage.Country;
import com.mthatcher.starcraft2wcs.LandingPage.MatchResult;
import com.mthatcher.starcraft2wcs.LandingPage.Race;
import com.mthatcher.starcraft2wcs.R;

public class GroupEntry implements GroupOrBracketEntry{
	private String name;
	private Country country;
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
		this.country = EntryUtil.getCountryFromString(country);
		this.race = EntryUtil.getRaceFromString(race);
		this.place = place.length() > 0 ? Integer.parseInt(place) : -1;
		this.matchesWon = matchesWon;
		this.matchesLost = matchesLost;
		this.mapsWon = mapsWon;
		this.mapsLost = mapsLost;
		this.result = getResultFromString(result);
	}

	public Country getCountry(){
		return country;
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

	public static ViewHolder getHolder(View convertView, int numEntrants) {
		ViewHolder holder = new ViewHolder();
		
		holder.isGroupHolder = true;
		
		holder.groupName = (TextView) convertView.findViewById(R.id.schedule_name);
		holder.date = (TextView) convertView.findViewById(R.id.schedule_date);
		
		holder.size = numEntrants;
		
		for(int i = 0; i < numEntrants; i++){
			holder.playerName.add(i, (TextView) convertView.findViewById(R.id.group_player_name));
			holder.rank.add(i, (TextView) convertView.findViewById(R.id.group_player_rank));
			holder.flag.add(i, (TextView) convertView.findViewById(R.id.group_player_flag));
			holder.matchScore.add(i, (TextView) convertView.findViewById(R.id.group_player_match_score));
			holder.mapScore.add(i, (TextView) convertView.findViewById(R.id.group_player_map_score));
				
		}
		
		return holder;
	}
}
