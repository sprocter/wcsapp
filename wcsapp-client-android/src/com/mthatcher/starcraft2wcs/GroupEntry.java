package com.mthatcher.starcraft2wcs;

import com.mthatcher.starcraft2wcs.LandingPage.MatchResult;
import com.mthatcher.starcraft2wcs.LandingPage.Race;

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
}
